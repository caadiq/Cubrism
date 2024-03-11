package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.SignInPostDTO;
import com.credential.cubrism.server.authentication.dto.SignInResultDTO;
import com.credential.cubrism.server.authentication.jwt.JwtTokenUtil;
import com.credential.cubrism.server.authentication.model.RefreshToken;
import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.service.AuthService;
import com.credential.cubrism.server.authentication.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SignInController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.secret}")
    private String secretKey;

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInPostDTO dto) {
        try {
            Users user = authService.signIn(dto);

            // 로그인 성공 => Jwt Token 발급
            long expireTimeMs = 1000 * 60 * 60; // Token 유효 시간 = 60분
            String token = JwtTokenUtil.createToken(user.getEmail(), secretKey, expireTimeMs);

            long RefreshexpireTimeMs = 1000 * 60 * 60 * 24 * 14; // RefreshToken 유효 시간 = 14일
            String refreshToken = JwtTokenUtil.createRefreshToken(user.getEmail(), secretKey, RefreshexpireTimeMs);

            refreshTokenService.deleteToken(user.getUuid());
            refreshTokenService.saveToken(refreshToken, user.getUuid());

            return ResponseEntity.ok().body(new SignInResultDTO(true, null, token, refreshToken));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SignInResultDTO(false, "이메일 또는 비밀번호를 확인해 주세요.", null,null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SignInResultDTO(false, e.getMessage(), null,null));
        }
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<?> refresh(@RequestHeader(value = "RefreshAuthorization", required = false) String refreshToken) {
//        if (refreshToken == null || refreshToken.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SignInResultDTO(false, "Refresh token is missing", null, null));
//        }
//
//        try {
//            // 리프레시 토큰 검증
////            if (JwtTokenUtil.isExpired(refreshToken, secretKey)) {
////                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is expired");
////            }
//
//            String loginId = JwtTokenUtil.getLoginId(refreshToken, secretKey);
//
//            // 새로운 액세스 토큰 발급
//            long expireTimeMs = 1000 * 60 * 600; // Token 유효 시간 = 600분
//            String newAccessToken = JwtTokenUtil.createToken(loginId, secretKey, expireTimeMs);
//
//            return ResponseEntity.ok().body(new SignInResultDTO(true, null, newAccessToken, null));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SignInResultDTO(false, e.getMessage(), null, null));
//        }
//    }

        @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(value = "RefreshAuthorization", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SignInResultDTO(false, "Refresh token is missing", null, null));
        }

        try {
            // 리프레시 토큰 검증
            RefreshToken savedToken = refreshTokenService.matches(refreshToken);
            if(savedToken == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SignInResultDTO(false, "Refresh token is invalid", null, null));
            }

            Users user = authService.getUserByUuid(savedToken.getUserId());
            String loginId = user.getEmail();

            // 새로운 액세스 토큰 발급
            long expireTimeMs = 1000 * 60 * 60; // Token 유효 시간 = 60분
            String newAccessToken = JwtTokenUtil.createToken(loginId, secretKey, expireTimeMs);

            return ResponseEntity.ok().body(new SignInResultDTO(true, null, newAccessToken, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SignInResultDTO(false, e.getMessage(), null, null));
        }
    }

    @GetMapping("/info")
    public String userInfo(Authentication auth) {
        Users loginUser = authService.getUserByEmail(auth.getName());

        return String.format("loginId : %s\nnickname : %s", loginUser.getEmail(), loginUser.getNickname());
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "관리자 페이지 접근 성공";
    }

    @GetMapping("/googleLoginTest")
    public ModelAndView test() {
        return new ModelAndView("googleLoginTest");
    }
}
