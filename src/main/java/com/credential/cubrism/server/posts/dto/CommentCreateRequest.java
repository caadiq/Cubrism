package com.credential.cubrism.server.posts.dto;


import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.posts.model.Comment;
import com.credential.cubrism.server.posts.model.Posts;
import lombok.Data;

@Data
public class CommentCreateRequest {

    private String content;

    public Comment toEntity(Posts post, Users user) {
        return Comment.builder()
                .user(user)
                .post(post)
                .content(content)
                .build();
    }
}
