package com.credential.cubrism.server.qualification.repository;

import com.credential.cubrism.server.qualification.entity.QualificationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface QualificationDetailsRepository extends JpaRepository<QualificationDetails, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM QualificationDetails q WHERE q.code = :code")
    void deleteByCode(@Param("code") String code);
}