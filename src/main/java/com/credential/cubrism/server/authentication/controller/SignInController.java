package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.SignInPostDTO;
import com.credential.cubrism.server.authentication.dto.SignInResultDTO;
import com.credential.cubrism.server.authentication.jwt.JwtTokenUtil;
import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SignInController {

    private final AuthService authService;

    @Value("${jwt.secret}")
    private String secretKey;

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInPostDTO dto) {
        try {
            Users user = authService.signIn(dto);

            // 로그인 성공 => Jwt Token 발급
            long expireTimeMs = 1000 * 60 * 600; // Token 유효 시간 = 600분
            String token = JwtTokenUtil.createToken(user.getEmail(), secretKey, expireTimeMs);

            return ResponseEntity.ok().body(new SignInResultDTO(true, null, token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SignInResultDTO(false, "이메일 또는 비밀번호를 확인해 주세요.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SignInResultDTO(false, e.getMessage(), null));
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
