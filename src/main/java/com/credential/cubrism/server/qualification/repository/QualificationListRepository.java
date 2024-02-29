package com.credential.cubrism.server.qualification.repository;

import com.credential.cubrism.server.qualification.model.QualificationList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualificationListRepository extends JpaRepository<QualificationList, String> {
    // 대직무분야명 중복 제거 후 반환
    @Query("SELECT DISTINCT q.majorFieldName FROM QualificationList q")
    List<String> findDistinctMajorFieldNames();

    // 중직무분야명에 해당하는 QualificationList 반환
    List<QualificationList> findByMajorFieldName(String majorFieldName);
}