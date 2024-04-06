package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

}
