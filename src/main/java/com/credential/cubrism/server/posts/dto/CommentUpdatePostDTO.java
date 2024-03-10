package com.credential.cubrism.server.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentUpdatePostDTO {
    private Long postId;
    private Long commentId;
    private String content;
}
