package com.credential.cubrism.server.authentication.validator;


import com.credential.cubrism.server.authentication.dto.UsersDTO;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class CheckSignUpValidator extends AbstractValidator<UsersDTO>{
    private final UserRepository userRepository;
    @Override
    protected void doValidate(UsersDTO dto, Errors errors) {
        if(userRepository.existsByEmail(dto.getEmail())) {
            errors.rejectValue("email", "이메일 중복 오류", "이미 사용중인 이메일입니다.");
        }
    }
}
