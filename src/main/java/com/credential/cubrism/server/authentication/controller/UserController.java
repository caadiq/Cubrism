package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.UserDTO;
import com.credential.cubrism.server.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public String registerUserAccount(@RequestBody UserDTO userDto) {
        try {
            userService.signUp(userDto);
            return "User registered successfully";
        } catch (Exception e) {
            return "Registration failed: " + e.getMessage();
        }
    }
}
