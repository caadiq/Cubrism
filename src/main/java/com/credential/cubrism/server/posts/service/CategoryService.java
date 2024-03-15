package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.posts.dto.CategoryGetDTO;
import com.credential.cubrism.server.posts.entity.Category;
import com.credential.cubrism.server.posts.entity.ElasticSearchCategory;
import com.credential.cubrism.server.posts.repository.CategoryRepository;
import com.credential.cubrism.server.posts.repository.ElasticSearchCategoryRepository;
import com.credential.cubrism.server.qualification.model.QualificationList;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final QualificationListRepository qualificationListRepository;
    private final ElasticSearchCategoryRepository elasticSearchCategoryRepository;

    public List<CategoryGetDTO> categoryList() {
        return StreamSupport.stream(elasticSearchCategoryRepository.findAll().spliterator(), false)
                .map(elasticSearchCategory -> new CategoryGetDTO(elasticSearchCategory.getCode(), elasticSearchCategory.getCategory()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveCategory() {
        categoryRepository.saveAll(qualificationListRepository.findAll().stream()
                .map(qualificationList -> {
                    Category category = new Category();
                    category.setCode(qualificationList.getCode());
                    category.setCategory(qualificationList.getName());
                    return category;
                })
                .collect(Collectors.toList()));
        saveCategoryToElasticSearch();
    }

    private void saveCategoryToElasticSearch() {
        List<QualificationList> qualificationList = qualificationListRepository.findAll();
        List<ElasticSearchCategory> elasticSearchCategories = new ArrayList<>();
        for (QualificationList qualification : qualificationList) {
            ElasticSearchCategory elasticSearchCategory = new ElasticSearchCategory();
            elasticSearchCategory.setCode(qualification.getCode());
            elasticSearchCategory.setCategory(qualification.getName());
            elasticSearchCategories.add(elasticSearchCategory);
        }
        elasticSearchCategoryRepository.saveAll(elasticSearchCategories);
    }
}
