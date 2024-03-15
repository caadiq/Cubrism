package com.credential.cubrism.server.posts.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PostImages")
public class PostImages {
    @Id
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Posts post;

    @Column(name = "image_index", nullable = false)
    private int imageIndex;
}
