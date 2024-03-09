package com.credential.cubrism.server.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostInfoGetDTO {
    private Long postId;
    private String boardName;
    private String nickname;
    private String title;
    private String content;
    private String createdDate;
    private List<PostImages> postImages;

    @Getter
    @AllArgsConstructor
    public static class PostImages {
        private String imageUrl;
    }
}
