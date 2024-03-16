package com.credential.cubrism.server.studygroup.repository;

import com.credential.cubrism.server.studygroup.entity.GroupMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupMembersRepository extends JpaRepository<GroupMembers, UUID> {

}
