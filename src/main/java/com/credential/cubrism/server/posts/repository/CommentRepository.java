package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {

}
