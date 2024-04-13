package com.credential.cubrism.server.studygroup.repository;

import com.credential.cubrism.server.studygroup.entity.StudyGroup;
import com.credential.cubrism.server.studygroup.entity.StudyGroupGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyGroupGoalRepository extends JpaRepository<StudyGroupGoal, Long> {
    List<StudyGroupGoal> findByStudyGroup(StudyGroup studyGroup);
}
