package com.credential.cubrism.server.qualification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QualificationListApiDTO {
    private String code;
    private String name;
    private String middleFieldName;
    private String majorFieldName;
    private String imageUrl;
}