package com.credential.cubrism.server.qualification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class QualificationListDto implements Serializable {
    private String code;
    private String name;
}
