package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.model.PostImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostImagesRepository extends JpaRepository<PostImages, UUID> {

}
