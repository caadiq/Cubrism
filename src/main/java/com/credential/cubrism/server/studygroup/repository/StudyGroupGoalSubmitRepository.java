package com.credential.cubrism.server.studygroup.repository;

import com.credential.cubrism.server.studygroup.entity.StudyGroupGoalSubmit;
import com.credential.cubrism.server.studygroup.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyGroupGoalSubmitRepository extends JpaRepository<StudyGroupGoalSubmit, Long> {
    List<StudyGroupGoalSubmit> findByUserGoal_StudyGroup_GroupId(Long groupId);

    Optional<Object> findByUserGoal(UserGoal userGoal);
}
