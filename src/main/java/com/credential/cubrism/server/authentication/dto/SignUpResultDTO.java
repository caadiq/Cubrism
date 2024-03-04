package com.credential.cubrism.server.authentication.dto;

import com.credential.cubrism.server.common.dto.FieldErrorDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SignUpResultDTO {
    private boolean success;
    private List<FieldErrorDTO> validation;
}