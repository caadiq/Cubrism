package com.credential.cubrism.server.studygroup.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoalDetailDto {
    private Long id;
    private String detail;

    public GoalDetailDto(Long id, String detail) {
        this.id = id;
        this.detail = detail;
    }
}
