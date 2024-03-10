package com.credential.cubrism.server.posts.dto;

import lombok.Getter;

@Getter
public class CommentAddPostDTO {
    private Long postId;
    private String content;
}