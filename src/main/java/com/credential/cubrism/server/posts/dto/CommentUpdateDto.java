package com.credential.cubrism.server.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentUpdateDto {
    private Long postId;
    private Long commentId;
    private String content;
}
