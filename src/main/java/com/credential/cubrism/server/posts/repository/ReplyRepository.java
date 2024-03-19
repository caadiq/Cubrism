package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.entity.Replies;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Replies, Long> {

}
