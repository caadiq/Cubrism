package com.credential.cubrism.server.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostMyListDto {
    private Pageable page;
    private List<PostList> postList;

    @Getter
    @AllArgsConstructor
    public static class Pageable {
        private Integer previousPage;
        private int currentPage;
        private Integer nextPage;
    }

    @Getter
    @AllArgsConstructor
    public static class PostList {
        private Long postId;
        private String category;
        private String nickname;
        private String profileImage;
        private List<String> images;
        private String title;
        private String content;
        private String createdDate;
        private Long commentCount;
    }
}
