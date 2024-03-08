package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.model.Board;
import com.credential.cubrism.server.posts.model.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Posts, UUID> {
    @Query("SELECT p FROM Posts p WHERE p.user.uuid = :userId AND p.postId = :postId")
    Optional<Posts> findByUserIdAndPostId(@Param("userId") UUID userId, @Param("postId") Long postId);

    Page<Posts> findAllByBoard(Board board, Pageable pageable);

    Page<Posts> findAllByUserUuid(UUID userId, Pageable pageable);

    @Query("SELECT p.title FROM Posts p")
    List<String> findAllTitles();

    @Query("SELECT p.title FROM Posts p WHERE p.user.uuid = :uuid")
    List<String> findAllTitlesByUuid(UUID uuid);

    Posts findByPostId(Long postId);
}

