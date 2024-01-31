package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.EmailDTO;
import com.credential.cubrism.server.authentication.dto.EmailResponseDTO;
import com.credential.cubrism.server.authentication.dto.EmailVerifyDTO;
import com.credential.cubrism.server.authentication.dto.EmailVerifyResponseDTO;
import com.credential.cubrism.server.authentication.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/signup/email")
class EmailController {
    private final EmailService emailService;

    // 이메일 인증 코드 요청
    @PostMapping("/request")
    public ResponseEntity<?> mailSend(@RequestBody @Valid EmailDTO emailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.badRequest().body(new EmailResponseDTO(false, error));
        }

        try {
            emailService.sendEmail(emailDto.getEmail()); // 인증 코드 이메일 전송
            return ResponseEntity.status(HttpStatus.CREATED).body(new EmailResponseDTO(true, null));
        } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EmailResponseDTO(false, null));
        }
    }

    // 이메일 인증 코드 확인
    @PostMapping("/verify")
    public ResponseEntity<?> emailCheck(@RequestBody @Valid EmailVerifyDTO emailVerifyDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.badRequest().body(new EmailVerifyResponseDTO(false, error));
        }

        try {
            emailService.verifyCode(emailVerifyDto.getEmail(), emailVerifyDto.getCode()); // 인증 코드 확인
            return ResponseEntity.status(HttpStatus.CREATED).body(new EmailVerifyResponseDTO(true, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EmailVerifyResponseDTO(false, e.getMessage()));
        }
    }
}