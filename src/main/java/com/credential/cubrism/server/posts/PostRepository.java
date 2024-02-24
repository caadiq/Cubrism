package com.credential.cubrism.server.posts;

import com.credential.cubrism.server.authentication.model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Posts, UUID> {

    List<Posts> findAllByUserUuid(UUID userId);

    @Query("SELECT p.title FROM Posts p")
    List<String> findAllTitles();

    @Query("SELECT p.title FROM Posts p WHERE p.user.uuid = :uuid")
    List<String> findAllTitlesByUuid(UUID uuid);
}
