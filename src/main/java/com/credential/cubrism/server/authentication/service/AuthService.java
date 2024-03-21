package com.credential.cubrism.server.authentication.service;

import com.credential.cubrism.server.authentication.dto.*;
import com.credential.cubrism.server.authentication.entity.Authority;
import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.jwt.JwtTokenProvider;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.authentication.utils.RedisUtil;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.s3.utils.S3Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshTokenExpiration;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    private final SecurityUtil securityUtil;
    private final RedisUtil redisUtil;
    private final S3Util s3Util;

    private static final String AUTHORITIES_KEY = "auth";
    private static final String REFRESH_TOKEN_SUFFIX = "(refreshToken)"; // Redis Key 중복 방지를 위한 접미사

    // 회원가입
    @Transactional
    public ResponseEntity<MessageDto> signUp(SignUpDto dto) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // ROLE_USER 권한 부여
        Authority authority = new Authority();
        authority.setAuthorityName("ROLE_USER");

        Users users = new Users();
        users.setEmail(dto.getEmail());
        users.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        users.setNickname(dto.getNickname());
        users.setAuthorities(Collections.singleton(authority));
        userRepository.save(users);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDto("회원가입에 성공했습니다."));
    }

    // 로그인 (이메일, 비밀번호)
    public ResponseEntity<TokenDto> signIn(SignInDto dto) {
        try {
            // 이메일과 비밀번호를 통해 인증 토큰 (JWT 아님) 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

            // AuthenticationManager를 통해 검증
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // 검증된 Authentication 객체를 통해 토큰 생성
            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken();

            // Redis에 Refresh Token 저장
            redisUtil.setData(authentication.getName() + REFRESH_TOKEN_SUFFIX, refreshToken, refreshTokenExpiration / 1000);

            return ResponseEntity.status(HttpStatus.OK).body(new TokenDto(accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            throw new CustomException(ErrorCode.BAD_CREDENTIALS);
        }
    }

    // 로그아웃
    public ResponseEntity<MessageDto> logOut() {
        Users currentUser = securityUtil.getCurrentUser();

        // Redis에서 Refresh Token 삭제
        redisUtil.deleteData(currentUser.getEmail() + REFRESH_TOKEN_SUFFIX);

        // TODO: 로그아웃 시 Access Token을 Redis 블랙리스트에 추가

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("로그아웃 완료"));
    }

    // 유저 정보
    public ResponseEntity<UserDto> userInfo() {
        Users currentUser = securityUtil.getCurrentUser();

        return ResponseEntity.status(HttpStatus.OK).body(new UserDto(
                currentUser.getEmail(),
                currentUser.getNickname(),
                currentUser.getImageUrl(),
                currentUser.getProvider()));
    }

    // Access Token 재발급
    public ResponseEntity<TokenDto> reIssueAccessToken(String accessToken, String refreshToken) {
        // Access Token 앞에 Bearer가 붙어있는지 확인하고 제거
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        } else {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        String subject = jwtTokenProvider.getClaims(accessToken).getSubject(); // 이메일
        String authorities = jwtTokenProvider.getClaims(accessToken).get(AUTHORITIES_KEY).toString(); // 권한 정보

        String storedRefreshToken = redisUtil.getData(subject + REFRESH_TOKEN_SUFFIX); // Redis에 저장된 Refresh Token을 가져옴

        // Refresh Token 유효성 검사
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken) || !jwtTokenProvider.validateToken(storedRefreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String newAccessToken = jwtTokenProvider.reissueAccessToken(subject, authorities); // Access Token 재발급

        return ResponseEntity.status(HttpStatus.OK).body(new TokenDto(newAccessToken, null));
    }

    // Refresh Token 재발급
    public ResponseEntity<TokenDto> reIssueRefreshToken() {
        Users currentUser = securityUtil.getCurrentUser();

        String storedRefreshToken = redisUtil.getData(currentUser.getEmail() + REFRESH_TOKEN_SUFFIX); // Redis에 저장된 Refresh Token을 가져옴

        // Refresh Token 유효성 검사
        if (storedRefreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String newRefreshToken = jwtTokenProvider.generateRefreshToken(); // Refresh Token 재발급
        redisUtil.setData(currentUser.getEmail() + REFRESH_TOKEN_SUFFIX, newRefreshToken, refreshTokenExpiration / 1000); // Redis에 새로운 Refresh Token 저장

        return ResponseEntity.status(HttpStatus.OK).body(new TokenDto(null, newRefreshToken));
    }

    // 비밀번호 변경
    @Transactional
    public ResponseEntity<MessageDto> changePassword(ChangePasswordDto dto) {
        Users user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

        // 새 비밀번호로 변경
        user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("비밀번호 변경 완료"));
    }

    // 회원탈퇴
    public ResponseEntity<?> withdrawal() {
        // TODO
        //  - 유저 정보 삭제
        //  - S3에 저장된 프로필 이미지 삭제
        //  - Redis에 저장된 Refresh Token 삭제
        //  - Access Token을 Redis 블랙리스트에 추가
        //  - 일정 삭제
        //  - 가입된 스터디 그룹 있으면 탈퇴
        //  - 게시글은 유지? 삭제?
        return null;
    }

    // 프로필 이미지 변경
    @Transactional
    public ResponseEntity<MessageDto> changeProfileImage(String imageUrl) {
        // S3에 파일이 존재하는지 확인
        if (!s3Util.isFileExists(imageUrl)) {
            throw new CustomException(ErrorCode.S3_FILE_NOT_FOUND);
        }

        Users currentUser = securityUtil.getCurrentUser();

        // 기존 프로필 이미지 삭제
        Optional.ofNullable(currentUser.getImageUrl())
                .ifPresent(s3Util::deleteFile);

        currentUser.setImageUrl(imageUrl);
        userRepository.save(currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("프로필 이미지 변경 완료"));
    }
}