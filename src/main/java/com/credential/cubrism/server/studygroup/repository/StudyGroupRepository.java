package com.credential.cubrism.server.studygroup.repository;

import com.credential.cubrism.server.studygroup.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    @Query("SELECT sg FROM StudyGroup sg JOIN sg.groupMembers gm WHERE gm.user.uuid = :userId")
    List<StudyGroup> findAllByUserId(UUID userId);
}
