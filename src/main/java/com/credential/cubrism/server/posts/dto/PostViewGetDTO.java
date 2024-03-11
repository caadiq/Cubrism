package com.credential.cubrism.server.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostViewGetDTO {
    private Long postId;
    private String boardName;
    private String nickname;
    private String email;
    private String title;
    private String content;
    private String createdDate;
    private List<Images> images;
    private List<Comments> comments;

    @Getter
    @AllArgsConstructor
    public static class Images {
        private String imageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class Comments {
        private Long commentId;
        private String nickname;
        private String email;
        private String profileImageUrl;
        private String content;
        private String createdDate;
    }
}
