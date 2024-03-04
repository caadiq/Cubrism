package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.SignUpPostDTO;
import com.credential.cubrism.server.authentication.dto.SignUpResultDTO;
import com.credential.cubrism.server.authentication.service.AuthService;
import com.credential.cubrism.server.authentication.validator.SignUpValidator;
import com.credential.cubrism.server.common.dto.FieldErrorDTO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/auth")
public class SignUpController {
    private final AuthService authService;
    private final SignUpValidator checksignUpvalidator;

    @InitBinder
    public void validatorBinder(WebDataBinder binder) {
        binder.addValidators(checksignUpvalidator);
    }

    @Autowired
    public SignUpController(AuthService authService, SignUpValidator checksignUpvalidator) {
        this.authService = authService;
        this.checksignUpvalidator = checksignUpvalidator;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpPostDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldErrorDTO> fieldErrors = bindingResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> new FieldErrorDTO(fieldError.getField(), fieldError.getDefaultMessage()))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SignUpResultDTO(false, fieldErrors));
        }

        try {
            authService.signUp(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new SignUpResultDTO(true, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SignUpResultDTO(false, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SignUpResultDTO(false, null));
        }
    }
}
