package com.credential.cubrism.server.authentication.service;

import com.credential.cubrism.server.authentication.dto.*;
import com.credential.cubrism.server.authentication.entity.Authority;
import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.jwt.JwtTokenProvider;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.authentication.utils.EmailUtil;
import com.credential.cubrism.server.authentication.utils.RedisUtil;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.s3.utils.S3Util;
import com.credential.cubrism.server.schedule.entity.Schedules;
import com.credential.cubrism.server.schedule.repository.ScheduleRepository;
import com.credential.cubrism.server.studygroup.entity.GroupMembers;
import com.credential.cubrism.server.studygroup.entity.StudyGroupGoal;
import com.credential.cubrism.server.studygroup.entity.UserGoal;
import com.credential.cubrism.server.studygroup.repository.GroupMembersRepository;
import com.credential.cubrism.server.studygroup.repository.StudyGroupGoalRepository;
import com.credential.cubrism.server.studygroup.repository.StudyGroupRepository;
import com.credential.cubrism.server.studygroup.repository.UserGoalRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.transaction.annotation.Transactional;
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
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshTokenExpiration;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${cubrism.logo.url}")
    private String logoUrl;

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupGoalRepository studyGroupGoalRepository;
    private final UserGoalRepository userGoalRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final WebClient webClient;

    private final SecurityUtil securityUtil;
    private final RedisUtil redisUtil;
    private final EmailUtil emailUtil;
    private final S3Util s3Util;

    private final HttpTransport transport = new NetHttpTransport();
    private final GsonFactory gsonFactory = new GsonFactory();

    private static final String AUTHORITIES_KEY = "auth";
    private static final String REFRESH_TOKEN_SUFFIX = "(refreshToken)"; // Redis Key 중복 방지를 위한 접미사
    private static final String RESET_PASSWORD_SUFFIX = "(resetPassword)"; // Redis Key 중복 방지를 위한 접미사
    private static final String GOOGLE_TOKEN_SERVER_ENCODED_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_REDIRECT_URI = "http://localhost:8080/login/oauth2/code/google";
    private static final String KAKAO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

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
        // 소셜 로그인 유저인지 확인
        userRepository.findByEmail(dto.getEmail()).filter(user -> user.getProvider() != null).ifPresent(user -> {
            throw new CustomException(ErrorCode.SOCIAL_LOGIN_USER);
        });

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
        try {
            Users currentUser = securityUtil.getCurrentUser();

            // Redis에 저장된 Refresh Token 삭제
            redisUtil.deleteData(currentUser.getEmail() + REFRESH_TOKEN_SUFFIX);

            return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("로그아웃 완료"));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.LOGOUT_FAILURE);
        }
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

    // 비밀번호 초기화 이메일 전송
    public ResponseEntity<MessageDto> findPassword(String email) {
        // 이메일 존재 여부 확인
        userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

        // 소셜 로그인 유저인지 확인
        userRepository.findByEmail(email)
                .filter(user -> user.getProvider() == null)
                .orElseThrow(() -> new CustomException(ErrorCode.SOCIAL_LOGIN_USER));

        try {
            String uuid = UUID.randomUUID().toString();
            emailUtil.sendResetPasswordEmail(email, uuid);
            redisUtil.setData(uuid + RESET_PASSWORD_SUFFIX, email, 300);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILURE);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("이메일을 전송했습니다."));
    }

    // 비밀번호 초기화 페이지
    public String resetPasswordPage(String uuid, Model model) {
        if (redisUtil.hasKey(uuid + RESET_PASSWORD_SUFFIX)) {
            model.addAttribute("uuid", uuid);
            model.addAttribute("logoUrl", logoUrl);
            model.addAttribute("errorMessage", null);
            return "reset_password";
        } else {
            model.addAttribute("logoUrl", logoUrl);
            return "reset_password_expired";
        }
    }

    public String resetPasswordPage(String uuid, Model model, String errorMessage) {
        if (redisUtil.hasKey(uuid + RESET_PASSWORD_SUFFIX)) {
            model.addAttribute("uuid", uuid);
            model.addAttribute("logoUrl", logoUrl);
            model.addAttribute("errorMessage", errorMessage);
            return "reset_password";
        } else {
            model.addAttribute("logoUrl", logoUrl);
            return "reset_password_expired";
        }
    }

    // 비밀번호 초기화
    public String resetPassword(String uuid, String newPassword, String confirmPassword, Model model) {

        System.out.println("uuid: " + uuid + ", newPassword: " + newPassword + ", confirmPassword: " + confirmPassword);
        String email = redisUtil.getData(uuid + RESET_PASSWORD_SUFFIX);
        if (email == null) {
            model.addAttribute("logoUrl", logoUrl);
            return "reset_password_expired";
        }

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_FOUND));

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);

        redisUtil.deleteData(uuid + RESET_PASSWORD_SUFFIX);

        model.addAttribute("logoUrl", logoUrl);
        return "reset_password_success";
    }

    @Transactional
    // 회원탈퇴
    public ResponseEntity<MessageDto> withdrawal() {
        // TODO
        //  - 유저 정보 삭제
        //  - S3에 저장된 프로필 이미지 삭제
        //  - Redis에 저장된 Refresh Token 삭제
        //  - 일정 삭제
        //  - 가입된 스터디 그룹 있으면 탈퇴 (그룹장이면 스터디 그룹 삭제)
        //  - 게시글, 댓글은 유지

        Users currentUser = securityUtil.getCurrentUser();

        List<Schedules> schedules = scheduleRepository.findByUserUuid(currentUser.getUuid());
        scheduleRepository.deleteAll(schedules);

        List<GroupMembers> memberships = groupMembersRepository.findByUser(currentUser);
        for (GroupMembers membership : memberships) {
            if (membership.isAdmin()) {
                // 사용자가 어드민인 경우, 해당 스터디 그룹을 삭제
                List<GroupMembers> members = groupMembersRepository.findByStudyGroup(membership.getStudyGroup());
                groupMembersRepository.deleteAll(members);
                List<UserGoal> userGoals = userGoalRepository.findByStudyGroup(membership.getStudyGroup());
                userGoalRepository.deleteAll(userGoals);
                List<StudyGroupGoal> studyGroupGoals = studyGroupGoalRepository.findByStudyGroup(membership.getStudyGroup());
                studyGroupGoalRepository.deleteAll(studyGroupGoals);
                studyGroupRepository.delete(membership.getStudyGroup());

            } else {
                // 사용자가 어드민이 아닌 경우, 사용자를 스터디 그룹에서 제거
                UserGoal userGoal = userGoalRepository.findByUserAndStudyGroup(currentUser, membership.getStudyGroup())
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_GOAL_NOT_FOUND));
                userGoalRepository.delete(userGoal);
                groupMembersRepository.delete(membership);
            }
        }

        userRepository.delete(currentUser);


        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("회원 탈퇴가 완료되었습니다."));
    }

    // 회원 정보 수정
    @Transactional
    public ResponseEntity<MessageDto> editUser(UserEditDto dto) {
        Users currentUser = securityUtil.getCurrentUser();
        
        if (dto.getImageUrl() != null) {
            // S3에 파일이 존재하는지 확인
            if (!s3Util.isFileExists(dto.getImageUrl())) {
                throw new CustomException(ErrorCode.S3_FILE_NOT_FOUND);
            }

            // 기존 프로필 이미지 삭제
            if (currentUser.getImageUrl() != null && s3Util.isFileExists(currentUser.getImageUrl())) {
                s3Util.deleteFile(currentUser.getImageUrl());
            }
        }
        
        currentUser.setNickname(dto.getNickname());
        currentUser.setImageUrl(dto.getImageUrl());
        userRepository.save(currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("회원 정보를 수정했습니다."));
    }

    // 구글 로그인
    @Transactional
    public ResponseEntity<TokenDto> googleLogIn(SocialTokenDto dto) {
        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    transport,
                    gsonFactory,
                    GOOGLE_TOKEN_SERVER_ENCODED_URL,
                    googleClientId,
                    googleClientSecret,
                    dto.getToken(),
                    GOOGLE_REDIRECT_URI
            ).execute();

            GoogleIdToken idToken = tokenResponse.parseIdToken();
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            return updateUserAndGenerateTokens(email, name, pictureUrl, "google");
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SIGNIN_FAILURE);
        }
    }

    // 카카오 로그인
    @Transactional
    public ResponseEntity<TokenDto> kakaoLogIn(SocialTokenDto dto) {
        ResponseEntity<KakaoUserDto> responseEntity = webClient.get()
                .uri(KAKAO_REQUEST_URL)
                .header("Authorization", "Bearer " + dto.getToken())
                .retrieve()
                .toEntity(KakaoUserDto.class)
                .block();

        if (responseEntity != null && responseEntity.getBody() != null) {
            KakaoUserDto kakaoUserDto = responseEntity.getBody();
            String email = kakaoUserDto.getKakao_account().getEmail();
            String nickname = kakaoUserDto.getKakao_account().getProfile().getNickname();
            String profileImageUrl = kakaoUserDto.getKakao_account().getProfile().getProfile_image_url();

            return updateUserAndGenerateTokens(email, nickname, profileImageUrl, "kakao");
        } else {
            throw new CustomException(ErrorCode.SIGNIN_FAILURE);
        }
    }

    // 소셜 로그인 유저 정보 업데이트 및 토큰 발급
    private ResponseEntity<TokenDto> updateUserAndGenerateTokens(String email, String nickname, String pictureUrl, String provider) {
        Users user = userRepository.findByEmail(email).orElseGet(() -> {
            Authority authority = new Authority();
            authority.setAuthorityName("ROLE_USER");

            Users newUser = new Users();
            newUser.setEmail(email);
            newUser.setNickname(nickname);
            newUser.setImageUrl(pictureUrl);
            newUser.setAuthorities(Collections.singleton(authority));
            return newUser;
        });

        user.setProvider(provider);
        userRepository.save(user);

        Set<Authority> authorities = user.getAuthorities();
        String accessToken = jwtTokenProvider.reissueAccessToken(email, authorities.toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        redisUtil.setData(email + REFRESH_TOKEN_SUFFIX, refreshToken, refreshTokenExpiration / 1000);

        return ResponseEntity.status(HttpStatus.OK).body(new TokenDto(accessToken, refreshToken));
    }
}