package com.credential.cubrism.server.authentication.model;

import com.credential.cubrism.server.posts.entity.Posts;
import com.credential.cubrism.server.schedule.model.Schedules;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    private UUID uuid;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "image_url")
    private String imageUrl;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

//    @ElementCollection //얘가 자동으로 테이블 만들어줌
//    @CollectionTable(name = "user_categories", joinColumns = @JoinColumn(name = "user_id"))
//    @Column(name = "category")
//    private List<String> categories;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Posts> posts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Schedules> schedules;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens;
}