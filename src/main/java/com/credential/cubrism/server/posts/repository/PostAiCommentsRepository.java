package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.entity.PostAiComments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostAiCommentsRepository extends JpaRepository<PostAiComments, Long> {
}
