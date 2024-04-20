package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.entity.PostImages;
import com.credential.cubrism.server.posts.entity.Posts;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImagesRepository extends JpaRepository<PostImages, String> {
    List<PostImages> findByPost(Posts post, Sort sort);
}
