package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.EmailRequestPostDTO;
import com.credential.cubrism.server.authentication.dto.EmailRequestResultDTO;
import com.credential.cubrism.server.authentication.dto.EmailVerifyPostDTO;
import com.credential.cubrism.server.authentication.dto.EmailVerifyResultDTO;
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
    public ResponseEntity<?> sendCode(@RequestBody @Valid EmailRequestPostDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new EmailRequestResultDTO(false, error));
        }

        try {
            emailService.sendEmail(dto.getEmail()); // 인증 코드 이메일 전송
            return ResponseEntity.ok().body(new EmailRequestResultDTO(true, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EmailRequestResultDTO(false, e.getMessage()));
        }
    }

    // 이메일 인증 코드 확인
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody @Valid EmailVerifyPostDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new EmailVerifyResultDTO(false, error));
        }

        try {
            emailService.verifyCode(dto.getEmail(), dto.getCode()); // 인증 코드 확인
            return ResponseEntity.ok().body(new EmailVerifyResultDTO(true, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new EmailVerifyResultDTO(false, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EmailVerifyResultDTO(false, null));
        }
    }
}