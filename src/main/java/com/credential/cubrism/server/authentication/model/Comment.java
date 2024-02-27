package com.credential.cubrism.server.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id")
    private Users user;      // 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_Id")
    private Posts post;    // 댓글이 달린 게시판

    public void update(String newBody) {
        this.body = newBody;
    }
}