package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.*;
import com.credential.cubrism.server.authentication.service.AuthService;
import com.credential.cubrism.server.common.dto.MessageDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

    @PostMapping("/logout") // 로그아웃
    public ResponseEntity<MessageDto> logOut() {
        return authService.logOut();
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

    @PostMapping("/profileimage") // 프로필 이미지 변경
    public ResponseEntity<MessageDto> changeProfileImage(@RequestBody ProfileImageDto dto) {
        return authService.changeProfileImage(dto.getImageUrl());
    }

    @GetMapping("/social")
    public ModelAndView socialTest() {
        return new ModelAndView("socialLoginTest");
    }
}
