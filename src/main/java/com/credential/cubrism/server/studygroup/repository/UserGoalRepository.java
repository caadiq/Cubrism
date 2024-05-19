package com.credential.cubrism.server.studygroup.repository;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.studygroup.entity.StudyGroup;
import com.credential.cubrism.server.studygroup.entity.StudyGroupGoal;
import com.credential.cubrism.server.studygroup.entity.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserGoalRepository extends JpaRepository<UserGoal, Long> {

    Optional<UserGoal> findByUserAndStudyGroup(Users user, StudyGroup studyGroup);

    List<UserGoal> findAllByUserAndStudyGroup(Users user, StudyGroup studyGroup);

    List<UserGoal> findByStudyGroup(StudyGroup studyGroup);

    List<UserGoal> findByStudyGroupGoal(StudyGroupGoal goal);
    Optional<UserGoal> findByUserAndStudyGroupGoal(Users user, StudyGroupGoal studyGroupGoal);

    Optional<UserGoal> findByUserAndStudyGroupAndStudyGroupGoal(Users currentUser, StudyGroup studyGroup, StudyGroupGoal goal);
}
