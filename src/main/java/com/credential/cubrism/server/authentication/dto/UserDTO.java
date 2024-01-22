package com.credential.cubrism.server.authentication.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserDTO {
    private UUID uuid;
    private String email;
    private String password;
    private String nickname;
}