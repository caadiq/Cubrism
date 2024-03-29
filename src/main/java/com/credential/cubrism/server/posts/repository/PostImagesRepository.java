package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.entity.PostImages;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostImagesRepository extends JpaRepository<PostImages, UUID> {
    @Query("SELECT pi FROM PostImages pi WHERE pi.post.postId = :postId ORDER BY pi.imageIndex")
    List<PostImages> findAllByPostId(@Param("postId") Long postId);

    void deleteByImageUrl(String imageUrl);
}
