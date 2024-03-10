package com.credential.cubrism.server.posts.dto;

import lombok.Getter;

@Getter
public class PostAddPostDTO {
    private String boardName;
    private String title;
    private String content;
}