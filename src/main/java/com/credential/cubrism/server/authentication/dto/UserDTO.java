package com.credential.cubrism.server.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserDTO {
    private UUID uuid;

    @Email(message = "이메일 형식에 맞게 입력해주세요.")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$", message = "비밀번호는 영문, 숫자, 특수문자를 혼합하여 8~16자리로 입력해주세요.")
    private String password;

    @Pattern(regexp = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{2,16}$" , message = "닉네임은 특수문자를 포함하지 않은 2~16자리로 입력해주세요.")
    private String nickname;
}