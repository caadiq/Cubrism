package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.entity.Board;
import com.credential.cubrism.server.posts.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Posts, UUID> {
    Page<Posts> findAllByBoard(Board board, Pageable pageable);

    Page<Posts> findAllByUserUuid(UUID userId, Pageable pageable);

    @Query("SELECT p FROM Posts p WHERE p.postId = :postId AND p.board.boardName = :boardName")
    Optional<Posts> findByPostIdAndBoardName(@Param("postId") Long postId, @Param("boardName") String boardName);

    Optional<Posts> findByPostId(Long postId);
}

