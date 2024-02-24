package com.credential.cubrism.server.posts.dto;

import com.credential.cubrism.server.authentication.model.Posts;
import com.credential.cubrism.server.authentication.model.Users;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostCreateRequestDTO {
    private String title;
    private String content;
    private String board_name;

    public Posts toEntity(Users user) {
        return Posts.builder()
                .board_name(this.board_name)
                .user(user)
                .title(this.title)
                .content(this.content)
                .build();
    }
}
