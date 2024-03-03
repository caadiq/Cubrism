package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.LoginRequestDTO;
import com.credential.cubrism.server.authentication.jwt.JwtTokenUtil;
import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jwt-login")
public class JwtLoginApiController {

    private final UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDTO loginRequestDTO) {

        Users user = userService.login(loginRequestDTO);

        // 로그인 아이디나 비밀번호가 틀린 경우 global error return
        if(user == null) {
            return"로그인 아이디 또는 비밀번호가 틀렸습니다.";
        }

        // 로그인 성공 => Jwt Token 발급

        String secretKey = "my-secret-key-123123";
        long expireTimeMs = 1000 * 60 * 600;     // Token 유효 시간 = 600분

        String jwtToken = JwtTokenUtil.createToken(user.getEmail(), secretKey, expireTimeMs);

        return jwtToken;
    }

    @GetMapping("/info")
    public String userInfo(Authentication auth) {
        Users loginUser = userService.getLoginUserByLoginId(auth.getName());

        return String.format("loginId : %s\nnickname : %s",
                loginUser.getEmail(), loginUser.getNickname());
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
