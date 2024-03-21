package com.credential.cubrism.server.posts.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostAddDto {
    private String boardName;
    private String title;
    private String content;
    private String category;
    private List<String> images;
}