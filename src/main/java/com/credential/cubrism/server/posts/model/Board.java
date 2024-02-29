package com.credential.cubrism.server.posts.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Getter
@Entity
@Table(name = "Board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "board_name", nullable = false)
    private String boardName;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Posts> posts;
}
