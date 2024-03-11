package com.credential.cubrism.server.authentication.service;

import com.credential.cubrism.server.authentication.dto.ProfileImageChangePostDTO;
import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.authentication.utils.AuthenticationUtil;
import com.credential.cubrism.server.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileImageService {
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public void changeProfileImage(ProfileImageChangePostDTO dto, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);

        String previousImageUrl = user.getImageUrl();
        user.setImageUrl(dto.getImageUrl());
        userRepository.save(user);

        if (previousImageUrl != null) {
            s3Service.deleteFileFromS3(previousImageUrl);
        }
    }
}
