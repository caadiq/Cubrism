package com.credential.cubrism.server.authentication.controller;

import com.credential.cubrism.server.authentication.service.ProfileImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
public class ProfileImageController {
    private final ProfileImageService profileImageService;

    @Autowired
    public ProfileImageController(ProfileImageService profileImageService) {
        this.profileImageService = profileImageService;
    }

    @PostMapping("/auth/profileimage")
    public ResponseEntity<?> postMember(
            @RequestParam("uuid") UUID uuid,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            return ResponseEntity.ok().body(profileImageService.uploadProfileImage(file, uuid));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
