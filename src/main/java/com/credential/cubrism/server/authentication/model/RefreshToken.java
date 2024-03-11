package com.credential.cubrism.server.authentication.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.UUID;

@Entity
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "token")
    private String token;

    protected RefreshToken() {
    }

    public RefreshToken(UUID userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public boolean validateSameToken(String token) {
        if (!this.token.equals(token)) {
            return false;
        } else {
            return true;
        }
    }
}
