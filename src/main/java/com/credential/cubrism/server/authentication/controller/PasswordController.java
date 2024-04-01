package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/auth/password")
public class PasswordController {
    private final AuthService authService;

    @GetMapping("/reset/{uuid}")
    public String resetPasswordPage(@PathVariable String uuid, Model model) {
        return authService.resetPasswordPage(uuid, model);
    }

    @PostMapping("/reset") // 비밀번호 초기화
    public String resetPassword(@RequestParam String uuid, @RequestParam String newPassword, @RequestParam String confirmPassword, Model model) {
        return authService.resetPassword(uuid, newPassword, confirmPassword, model);
    }
}
