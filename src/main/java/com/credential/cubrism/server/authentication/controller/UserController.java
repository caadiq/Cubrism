package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.CheckSignUpValidator;
import com.credential.cubrism.server.authentication.dto.UserDTO;
import com.credential.cubrism.server.authentication.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    public String registerUserAccount(@RequestBody @Valid UserDTO userDto, Errors errors, Model model) {
        if (errors.hasErrors()){ // 유효성 검사 실패. 즉, 중복되는게 있을 경우


            return "duplication error";
        }
        try {
            userService.signUp(userDto);
            return "User registered successfully";
        } catch (Exception e) {
            return "Registration failed: " + e.getMessage();
        }
    }

}
