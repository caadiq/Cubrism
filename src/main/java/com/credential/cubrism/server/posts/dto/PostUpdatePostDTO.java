package com.credential.cubrism.server.posts.dto;

import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
public class PostUpdatePostDTO {
    private Long postId;
    private String boardName;
    private String title;
    private String content;
    private List<Images> images;
    @Nullable
    private List<RemovedImages> removedImages;

    @Getter
    public static class Images {
        private String imageUrl;
    }

    @Getter
    public static class RemovedImages {
        private String imageUrl;
    }
}
