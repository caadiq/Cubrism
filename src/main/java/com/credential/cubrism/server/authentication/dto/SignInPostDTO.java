package com.credential.cubrism.server.authentication.dto;

import lombok.Getter;

@Getter
public class SignInPostDTO {
    private String email;
    private String password;
}
