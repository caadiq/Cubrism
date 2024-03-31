package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.*;
import com.credential.cubrism.server.authentication.service.AuthService;
import com.credential.cubrism.server.common.dto.MessageDto;
import jakarta.servlet.http.HttpServletRequest;
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

    @DeleteMapping("/logout") // 로그아웃
    public ResponseEntity<MessageDto> logOut(HttpServletRequest request) {
        return authService.logOut(request);
    }

    @GetMapping("/info") // 로그인 유저 정보
    public ResponseEntity<UserDto> info() {
        return authService.userInfo();
    }

    @GetMapping("/reissue-access-token") // Access Token 재발급
    public ResponseEntity<TokenDto> reIssueAccessToken(
            @RequestHeader(value = "AccessToken") String accessToken,
            @RequestHeader(value = "RefreshToken") String refreshToken
    ) {
        return authService.reIssueAccessToken(accessToken, refreshToken);
    }

    @GetMapping("/reissue-refresh-token") // Refresh Token 재발급
    public ResponseEntity<TokenDto> reIssueRefreshToken() {
        return authService.reIssueRefreshToken();
    }

    @PostMapping("/password") // 비밀번호 변경
    public ResponseEntity<MessageDto> changePassword(@RequestBody @Valid ChangePasswordDto dto) {
        return authService.changePassword(dto);
    }

    @PostMapping("/withdrawal") // 회원탈퇴
    public ResponseEntity<?> withdrawal() {
        return authService.withdrawal();
    }

    @PostMapping("/user/edit") // 회원 정보 수정
    public ResponseEntity<MessageDto> editUser(@RequestBody UserEditDto dto) {
        return authService.editUser(dto);
    }

    @PostMapping("/social/login/google")
    public ResponseEntity<TokenDto> googleLogIn(@RequestParam String serverAuthCode) {
        return authService.googleLogIn(serverAuthCode);
    }

    @PostMapping("/social/login/kakao")
    public ResponseEntity<TokenDto> kakaoLogIn(@RequestParam String accessToken) {
        return authService.kakaoLogIn(accessToken);
    }
}
