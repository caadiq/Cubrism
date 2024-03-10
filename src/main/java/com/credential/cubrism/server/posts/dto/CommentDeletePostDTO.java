package com.credential.cubrism.server.posts.dto;

import lombok.Getter;

@Getter
public class CommentDeletePostDTO {
    private Long postId;
    private Long commentId;
}