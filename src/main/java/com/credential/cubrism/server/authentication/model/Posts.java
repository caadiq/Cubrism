package com.credential.cubrism.server.authentication.model;

import com.credential.cubrism.server.authentication.model.Users;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "Posts")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "board_name", nullable = false)
    private String board_name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "post_title", nullable = false)
    private String title;

    @Column(name = "post_content", nullable = false)
    private String content;

    @Column(name = "category", nullable = true)
    private String category;
}
