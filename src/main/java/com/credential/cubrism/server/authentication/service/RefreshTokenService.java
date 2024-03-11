package com.credential.cubrism.server.authentication.service;

import com.credential.cubrism.server.authentication.jwt.JwtTokenUtil;
import com.credential.cubrism.server.authentication.model.RefreshToken;
import com.credential.cubrism.server.authentication.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Transactional
    public void saveToken(String refreshToken, UUID uuid) {
        RefreshToken token = new RefreshToken(uuid, refreshToken);

        refreshTokenRepository.save(token);

    }

    @Transactional
    public void deleteToken(UUID uuid) {
        if(refreshTokenRepository.existsByUserId(uuid)){
            refreshTokenRepository.deleteByUserId(uuid);
        }
    }
    @Transactional
    public RefreshToken matches(String refreshToken) {
        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken);
        if (savedToken == null) {
            return null;
        }
        if (JwtTokenUtil.isExpired(savedToken.getToken(), secretKey)) {
            refreshTokenRepository.delete(savedToken);
            return null;
        }
        if (!savedToken.validateSameToken(refreshToken)) {
            return null;
        }

        return savedToken;
    }
}
