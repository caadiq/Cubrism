package com.credential.cubrism.server.qualification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QualificationDetailsRequestDTO {
    private String code;
    private String name;
}
