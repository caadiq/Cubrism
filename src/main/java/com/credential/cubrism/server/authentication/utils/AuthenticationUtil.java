package com.credential.cubrism.server.authentication.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.credential.cubrism.server.authentication.oauth.PrincipalDetails;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.authentication.model.Users;

public class AuthenticationUtil {
    public static Users getUserFromAuthentication(Authentication authentication, UserRepository userRepository) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof PrincipalDetails principalDetails) {
            return principalDetails.user();
        } else if (principal instanceof String email) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + principal));
        } else {
            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass().getName());
        }
    }
}
