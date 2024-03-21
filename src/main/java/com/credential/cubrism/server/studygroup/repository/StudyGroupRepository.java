package com.credential.cubrism.server.studygroup.repository;

import com.credential.cubrism.server.studygroup.entity.StudyGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    @Query("SELECT sg FROM StudyGroup sg JOIN sg.groupMembers gm WHERE gm.user.uuid = :userId")
    Page<StudyGroup> findByUserId(UUID userId, Pageable pageable);
}
