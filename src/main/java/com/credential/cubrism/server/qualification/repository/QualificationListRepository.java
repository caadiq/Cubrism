package com.credential.cubrism.server.qualification.repository;

import com.credential.cubrism.server.qualification.model.QualificationList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualificationListRepository extends JpaRepository<QualificationList, String> {

}