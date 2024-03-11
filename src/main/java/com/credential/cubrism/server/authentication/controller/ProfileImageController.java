package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.dto.ProfileImageChangePostDTO;
import com.credential.cubrism.server.authentication.service.ProfileImageService;
import com.credential.cubrism.server.common.dto.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileImageController {
    private final ProfileImageService profileImageService;

    @Autowired
    public ProfileImageController(ProfileImageService profileImageService) {
        this.profileImageService = profileImageService;
    }

    @PostMapping("/auth/profileimage")
    public ResponseEntity<?> uploadProfileImage(
            @RequestBody ProfileImageChangePostDTO dto,
            Authentication authentication
    ) {
        try {
            profileImageService.changeProfileImage(dto, authentication);
            return ResponseEntity.ok().body(new ResultDTO(true, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResultDTO(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResultDTO(false, e.getMessage()));
        }
    }
}
