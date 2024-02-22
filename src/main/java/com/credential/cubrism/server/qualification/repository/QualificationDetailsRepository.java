package com.credential.cubrism.server.qualification.repository;

import com.credential.cubrism.server.qualification.model.QualificationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface QualificationDetailsRepository extends JpaRepository<QualificationDetails, String> {
    @Transactional
    void deleteByCode(String code);
}