package com.credential.cubrism.server.posts.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterPostRequestDTO {
    private Long postId;
    private String boardName;
    private String title;
    private String content;
}
