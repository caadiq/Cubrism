package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost_PostId(Long postId);
//    List<Comment> findAllByUserLoginId(String loginId);
}
