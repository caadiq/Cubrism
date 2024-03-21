package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.EmailDto;
import com.credential.cubrism.server.authentication.dto.EmailVerifyDto;
import com.credential.cubrism.server.authentication.service.EmailService;
import com.credential.cubrism.server.common.dto.MessageDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/signup/email")
class EmailController {
    private final EmailService emailService;

    @PostMapping("/request") // 이메일 인증번호 요청
    public ResponseEntity<MessageDto> sendCode(@RequestBody @Valid EmailDto dto) {
        return emailService.sendEmail(dto.getEmail());
    }

    @PostMapping("/verify") // 이메일 인증번호 확인
    public ResponseEntity<MessageDto> verifyCode(@RequestBody @Valid EmailVerifyDto dto) {
        return emailService.verifyCode(dto.getEmail(), dto.getCode());
    }
}