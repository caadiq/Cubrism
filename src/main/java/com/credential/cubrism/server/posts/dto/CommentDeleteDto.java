package com.credential.cubrism.server.posts.dto;

import lombok.Getter;

@Getter
public class CommentDeleteDto {
    private Long postId;
    private Long commentId;
}