package com.credential.cubrism.server.elasticsearch.service;

import com.credential.cubrism.server.elasticsearch.domain.ElasticSearchCategory;
import com.credential.cubrism.server.elasticsearch.repository.ElasticSearchCategoryRepository;
import com.credential.cubrism.server.qualification.model.QualificationList;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticSearchCategoryService {
    private final ElasticSearchCategoryRepository elasticSearchCategoryRepository;
    private final QualificationListRepository qualificationListRepository;

    public void saveAllQualifications() {
        List<QualificationList> qualificationList = qualificationListRepository.findAll();
        List<ElasticSearchCategory> elasticSearchCategories = new ArrayList<>();
        for (QualificationList qualification : qualificationList) {
            ElasticSearchCategory elasticSearchCategory = new ElasticSearchCategory();
            elasticSearchCategory.setCode(qualification.getCode());
            elasticSearchCategory.setName(qualification.getName());
            elasticSearchCategories.add(elasticSearchCategory);
        }
        elasticSearchCategoryRepository.saveAll(elasticSearchCategories);
    }

    public List<ElasticSearchCategory> getAllQualifications() {
        Iterable<ElasticSearchCategory> iterable = elasticSearchCategoryRepository.findAll(Sort.by("code"));
        List<ElasticSearchCategory> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
