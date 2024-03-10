package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {
    @Query("SELECT c FROM Comments c WHERE c.user.uuid = :userId AND c.commentId = :commentId")
    Optional<Comments> findByUserIdAndCommentId(@Param("userId") UUID userId, @Param("commentId") Long commentId);
}
