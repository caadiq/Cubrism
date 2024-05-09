package com.credential.cubrism.server.notification.entity;

import com.credential.cubrism.server.authentication.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "FcmTokens")
public class FcmTokens {
    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token", nullable = false)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @MapsId
    private Users user;
}
