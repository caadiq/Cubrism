package com.credential.cubrism.server.studygroup.repository;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.studygroup.entity.StudyGroup;
import com.credential.cubrism.server.studygroup.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {

    Optional<UserGoal> findByUserAndStudyGroupGoal_StudyGroup(Users user, StudyGroup studyGroup);
}
