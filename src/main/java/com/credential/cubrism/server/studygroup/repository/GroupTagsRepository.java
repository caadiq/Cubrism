package com.credential.cubrism.server.studygroup.repository;

import com.credential.cubrism.server.studygroup.entity.GroupTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupTagsRepository extends JpaRepository<GroupTags, Long> {

}
