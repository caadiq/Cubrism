package com.credential.cubrism.server.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class CategoryListGetDTO implements Serializable {
    private String code;
    private String category;
}
