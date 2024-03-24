package com.credential.cubrism.server.studygroup.repository;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.studygroup.entity.PendingMembers;
import com.credential.cubrism.server.studygroup.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PendingMembersRepository extends JpaRepository<PendingMembers, UUID> {
    List<PendingMembers> findAllByRequestDateBefore(LocalDateTime date);
    Optional<PendingMembers> findByUserAndStudyGroup(Users user, StudyGroup studyGroup);
    List<PendingMembers> findByStudyGroup(StudyGroup studyGroup);
}
