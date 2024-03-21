package com.credential.cubrism.server.authentication.jwt;

import org.springframework.security.oauth2.jwt.JwtException;

public class JwtTokenException extends JwtException {
    public JwtTokenException(String message) {
        super(message);
    }
}