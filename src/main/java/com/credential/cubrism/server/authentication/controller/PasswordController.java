package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.ResetPasswordConfirmDto;
import com.credential.cubrism.server.authentication.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@AllArgsConstructor
@RequestMapping("/auth/users/password")
public class PasswordController {
    private final AuthService authService;

    @GetMapping("/reset/{uuid}")
    public String resetPasswordPage(@PathVariable String uuid, Model model) {
        return authService.resetPasswordPage(uuid, model);
    }

    @PostMapping("/reset") // 비밀번호 초기화
    public String resetPassword(@Valid @ModelAttribute ResetPasswordConfirmDto resetPasswordConfirmDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            String passwordErrorMessage = Objects.requireNonNull(bindingResult.getFieldError("newPassword")).getDefaultMessage();
            return authService.resetPasswordPage(resetPasswordConfirmDto.getUuid(), model, passwordErrorMessage);
        }
        return authService.resetPassword(resetPasswordConfirmDto.getUuid(), resetPasswordConfirmDto.getNewPassword(), resetPasswordConfirmDto.getConfirmPassword(), model);
    }

}
