package com.credential.cubrism.server.notification.repository;

import com.credential.cubrism.server.notification.entity.FcmTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FcmRepository extends JpaRepository<FcmTokens, UUID> {
    Optional<FcmTokens> findByUserId(UUID userId);

    void deleteByUserId(UUID uuid);
}
