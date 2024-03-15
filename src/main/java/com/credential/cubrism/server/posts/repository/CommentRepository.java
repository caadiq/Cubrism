package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {
    Optional<Comments> findByCommentId(Long commentId);

    @Query("SELECT c FROM Comments c WHERE c.post.postId = :postId AND c.commentId = :commentId")
    Optional<Comments> findByPostIdAndCommentId(@Param("postId") Long postId, @Param("commentId") Long commentId);
}
