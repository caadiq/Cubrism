package com.credential.cubrism.server.authentication.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSaveDTO {
    private UUID uuid;
    @NotBlank(message="이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
} //형식 만들었는데, 얘는 나중에 쓰겠음. 일단은 기존의 UserDTO 먼저 쓸게
