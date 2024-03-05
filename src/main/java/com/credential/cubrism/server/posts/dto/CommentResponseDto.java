package com.credential.cubrism.server.posts.dto;

import com.credential.cubrism.server.posts.model.Comment;
import lombok.Getter;

@Getter
public class CommentResponseDto {
    private String commentContent;
    private String userName;

    public CommentResponseDto(Comment comment) {
        this.commentContent = comment.getContent();
        this.userName = comment.getUser().getNickname();
    }
    // getters and setters
}
