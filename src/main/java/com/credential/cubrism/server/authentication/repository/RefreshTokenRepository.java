package com.credential.cubrism.server.authentication.repository;


import com.credential.cubrism.server.authentication.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByUserId(UUID userId);

    RefreshToken findByToken(String token);

    boolean existsByUserId(UUID userId);
    void deleteByUserId(UUID uuid);
}
