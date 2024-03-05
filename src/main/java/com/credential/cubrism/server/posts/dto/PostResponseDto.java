package com.credential.cubrism.server.posts.dto;

import com.credential.cubrism.server.posts.model.Posts;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
public class PostResponseDto {
    private String boardName;
    private String title;
    private String content;
    private String userName;
    private List<CommentResponseDto> comments;

    public PostResponseDto(Posts post) {
        this.boardName = post.getBoard().getBoardName();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userName = post.getUser().getNickname();
        this.comments = post.getComments().stream()
                .map(comment -> new CommentResponseDto(comment))
                .collect(Collectors.toList());
    }
}
