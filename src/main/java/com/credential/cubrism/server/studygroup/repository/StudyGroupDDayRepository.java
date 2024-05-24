package com.credential.cubrism.server.studygroup.repository;

import com.credential.cubrism.server.studygroup.entity.StudyGroup;
import com.credential.cubrism.server.studygroup.entity.StudyGroupDDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyGroupDDayRepository extends JpaRepository<StudyGroupDDay, Long> {
    Optional<StudyGroupDDay> findByStudyGroup(StudyGroup studyGroup);
}
