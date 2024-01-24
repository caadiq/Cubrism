package com.credential.cubrism.server.authentication.validator;


import com.credential.cubrism.server.authentication.dto.UserDTO;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class CheckSignUpValidator extends AbstractValidator<UserDTO>{
    private final UserRepository userRepository;
    @Override
    protected void doValidate(UserDTO dto, Errors errors) {
        if(userRepository.existsByEmail(dto.getEmail())) {
            errors.rejectValue("email", "이메일 중복 오류", "이미 사용중인 이메일입니다.");
        }
        /* //닉네임 중복 확인 제외함. 필요시 주석 풀 것
        if (userRepository.existsByNickname(dto.getNickname())) {
            errors.rejectValue("nickname", "닉네임 중복 오류", "이미 사용중인 닉네임입니다.");
        }
        */

    }
}
