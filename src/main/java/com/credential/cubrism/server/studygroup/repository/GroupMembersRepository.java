package com.credential.cubrism.server.studygroup.repository;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.studygroup.entity.GroupMembers;
import com.credential.cubrism.server.studygroup.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupMembersRepository extends JpaRepository<GroupMembers, UUID> {
    Optional<GroupMembers> findByUserAndStudyGroup(Users user, StudyGroup studyGroup);
    List<GroupMembers> findAllByUserAndAdmin(Users user, boolean isAdmin);

    Optional<GroupMembers> findByUserAndStudyGroupAndAdmin(Users user, StudyGroup studyGroup, boolean admin);
}
