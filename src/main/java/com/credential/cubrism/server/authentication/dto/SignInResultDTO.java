package com.credential.cubrism.server.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInResultDTO {
    private boolean success;
    private String message;
    private String token;
    private String refreshToken;
}
