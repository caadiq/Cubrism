package com.credential.cubrism.server.favorites.repository;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.favorites.entity.Favorites;
import com.credential.cubrism.server.qualification.entity.QualificationList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorites, Long> {
    boolean existsByUserAndQualificationList(Users user, QualificationList qualificationList);
    List<Favorites> findALlByUserUuid(UUID userId);
}
