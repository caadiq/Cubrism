package com.credential.cubrism.server.studygroup.repository;

import com.credential.cubrism.server.studygroup.entity.StudyGroupGoalSubmit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyGroupGoalSubmitRepository extends JpaRepository<StudyGroupGoalSubmit, Long> {
    List<StudyGroupGoalSubmit> findByUserGoal_StudyGroup_GroupId(Long groupId);
}
