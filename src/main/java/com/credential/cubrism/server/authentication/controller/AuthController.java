package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.*;
import com.credential.cubrism.server.authentication.service.AuthService;
import com.credential.cubrism.server.common.dto.MessageDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup") // 회원가입
    public ResponseEntity<MessageDto> signUp(@RequestBody @Valid SignUpDto dto) {
        return authService.signUp(dto);
    }

    @PostMapping("/signin") // 로그인
    public ResponseEntity<TokenDto> signIn(@RequestBody @Valid SignInDto dto) {
        return authService.signIn(dto);
    }

    @PostMapping("/signin/google") // 구글 로그인
    public ResponseEntity<TokenDto> googleLogIn(@RequestBody SocialTokenDto dto) {
        return authService.googleLogIn(dto);
    }

    @PostMapping("/signin/kakao") // 카카오 로그인
    public ResponseEntity<TokenDto> kakaoLogIn(@RequestBody SocialTokenDto dto) {
        return authService.kakaoLogIn(dto);
    }

    @PostMapping("/logout") // 로그아웃
    public ResponseEntity<MessageDto> logOut() {
        return authService.logOut();
    }

    @GetMapping("/users") // 로그인 유저 정보
    public ResponseEntity<UserDto> info() {
        return authService.userInfo();
    }

    @PutMapping("/users/password") // 비밀번호 변경
    public ResponseEntity<MessageDto> changePassword(@RequestBody @Valid ChangePasswordDto dto) {
        return authService.changePassword(dto);
    }

    @PostMapping("/users/password") // 비밀번호 초기화 이메일 전송
    public ResponseEntity<MessageDto> findPassword(@RequestBody @Valid ResetPasswordDto dto) {
        return authService.findPassword(dto.getEmail());
    }

    @DeleteMapping("/users") // 회원탈퇴
    public ResponseEntity<?> withdrawal() {
        return authService.withdrawal();
    }

    @PutMapping("/users") // 회원 정보 수정
    public ResponseEntity<MessageDto> editUser(@RequestBody UserEditDto dto) {
        return authService.editUser(dto);
    }

    @PostMapping("/token/access") // Access Token 재발급
    public ResponseEntity<TokenDto> reIssueAccessToken(
            @RequestHeader(value = "AccessToken") String accessToken,
            @RequestHeader(value = "RefreshToken") String refreshToken
    ) {
        return authService.reIssueAccessToken(accessToken, refreshToken);
    }

    @PostMapping("/token/refresh") // Refresh Token 재발급
    public ResponseEntity<TokenDto> reIssueRefreshToken() {
        return authService.reIssueRefreshToken();
    }
}
