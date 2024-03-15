package com.credential.cubrism.server.authentication.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class RefreshToken {

    @Id
    private UUID userId;

    @Column(name = "refresh_token")
    private String token;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "FK_RefreshToken_userId", value = ConstraintMode.CONSTRAINT))
    private Users user;

    protected RefreshToken() {
    }

    public RefreshToken(Users user, String token) {
        this.user = user;
        this.userId = user.getUuid();
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
