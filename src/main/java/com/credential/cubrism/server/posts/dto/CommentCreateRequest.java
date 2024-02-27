package com.credential.cubrism.server.posts.dto;

import com.credential.cubrism.server.authentication.model.Comment;
import com.credential.cubrism.server.authentication.model.Posts;
import com.credential.cubrism.server.authentication.model.Users;
import lombok.Data;

@Data
public class CommentCreateRequest {

    private String body;

    public Comment toEntity(Posts post, Users user) {
        return Comment.builder()
                .user(user)
                .post(post)
                .body(body)
                .build();
    }
}
