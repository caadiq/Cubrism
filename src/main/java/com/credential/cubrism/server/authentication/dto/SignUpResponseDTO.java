package com.credential.cubrism.server.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SignUpResponseDTO {
    private boolean success;
    private List<FieldErrorDTO> validation;
}