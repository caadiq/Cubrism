package com.credential.cubrism.server.posts.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostUpdateDto {
    private Long postId;
    private String boardName;
    private String title;
    private String content;
    private List<String> images;
    private List<String> removedImages;
}
