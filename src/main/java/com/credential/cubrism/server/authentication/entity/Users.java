package com.credential.cubrism.server.authentication.entity;

import com.credential.cubrism.server.favorites.entity.Favorites;
import com.credential.cubrism.server.notification.entity.FcmTokens;
import com.credential.cubrism.server.posts.entity.Posts;
import com.credential.cubrism.server.schedule.entity.Schedules;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @Column(name = "provider")
    private String provider;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

    @ManyToMany
    @JoinTable(
            name = "UserAuthority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Posts> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Schedules> schedules;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Favorites> favorites;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private FcmTokens fcmTokens;
}