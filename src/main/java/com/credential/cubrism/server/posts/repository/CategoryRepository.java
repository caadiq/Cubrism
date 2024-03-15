package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByCategory(String category);
}
