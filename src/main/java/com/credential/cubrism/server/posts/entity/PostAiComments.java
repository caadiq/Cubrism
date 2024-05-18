package com.credential.cubrism.server.posts.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PostAiComments")
@Getter
@Setter
public class PostAiComments {
    @Id
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name= "content")
    String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @MapsId
    private Posts post;

}
