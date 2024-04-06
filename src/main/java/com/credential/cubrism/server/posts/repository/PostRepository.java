package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.entity.Board;
import com.credential.cubrism.server.posts.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Posts, Long> {
    Page<Posts> findAllByBoard(Board board, Pageable pageable);

    @Query("SELECT p FROM Posts p WHERE p.board = :board AND (LOWER(REPLACE(p.title, ' ', '')) LIKE %:searchQuery% OR LOWER(REPLACE(p.content, ' ', '')) LIKE %:searchQuery% OR LOWER(REPLACE(p.qualificationList.name, ' ', '')) LIKE %:searchQuery%)")
    Page<Posts> findAllByBoardAndSearchQuery(Board board, String searchQuery, Pageable pageable);

    Page<Posts> findAllByUserUuid(UUID userId, Pageable pageable);
}