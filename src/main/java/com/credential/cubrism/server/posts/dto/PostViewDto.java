package com.credential.cubrism.server.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostViewDto {
    private Long postId;
    private String boardName;
    private String category;
    private String nickname;
    private String profileImageUrl;
    private String email;
    private String title;
    private String content;
    private String createdDate;
    private String modifiedDate;
    private List<String> images;
    private List<Comments> comments;

    @Getter
    @AllArgsConstructor
    public static class Comments {
        private Long commentId;
        private Long replyTo;
        private String replyToNickname;
        private String nickname;
        private String email;
        private String content;
        private String createdDate;
        private String profileImageUrl;
        private Boolean isUpdated;
    }
}
