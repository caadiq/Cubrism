package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.FieldErrorDTO;
import com.credential.cubrism.server.authentication.dto.UserDTO;
import com.credential.cubrism.server.authentication.service.UserService;
import com.credential.cubrism.server.authentication.validator.CheckSignUpValidator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserController {
    private final UserService userService;
    private final CheckSignUpValidator checksignUpvalidator;

    @InitBinder
    public void validatorBinder(WebDataBinder binder) {
        binder.addValidators(checksignUpvalidator);
    }

    @Autowired
    public UserController(UserService userService, CheckSignUpValidator checksignUpvalidator) {
        this.userService = userService;
        this.checksignUpvalidator = checksignUpvalidator;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUserAccount(@RequestBody @Valid UserDTO userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 유효성 검사 실패 시 오류 메시지 반환
            List<FieldErrorDTO> fieldErrors = bindingResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> new FieldErrorDTO(fieldError.getField(), fieldError.getDefaultMessage()))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(fieldErrors, HttpStatus.BAD_REQUEST);
        }

        try {
            userService.signUp(userDto);
            return new ResponseEntity<>("회원가입 성공!", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("회원가입 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
