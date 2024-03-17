package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByCategory(String category);

    @Query("SELECT c FROM Category c WHERE c.category LIKE %:category%")
    List<Category> findByCategoryName(@Param("category") String category);
}
