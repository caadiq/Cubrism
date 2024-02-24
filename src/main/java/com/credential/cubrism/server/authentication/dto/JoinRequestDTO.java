package com.credential.cubrism.server.authentication.dto;

import com.credential.cubrism.server.authentication.model.Users;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequestDTO {

    @NotBlank(message = "이메일이 비어있습니다.")
    private String email;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "닉네임이 비어있습니다.")
    private String nickname;

    // 비밀번호 암호화 X
    public Users toEntity() {
        return Users.builder()
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .build();
    }

    // 비밀번호 암호화
    public Users toEntity(String encodedPassword) {
        return Users.builder()
                .email(this.email)
                .password(encodedPassword)
                .nickname(this.nickname)
                .build();
    }
}