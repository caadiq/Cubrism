package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {
    Optional<Comments> findByCommentId(Long commentId);
}
