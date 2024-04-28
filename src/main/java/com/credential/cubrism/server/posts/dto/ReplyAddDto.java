package com.credential.cubrism.server.posts.dto;

import lombok.Getter;

@Getter
public class ReplyAddDto {
    private Long postId;
    private Long commentId;
    private String content;
}
