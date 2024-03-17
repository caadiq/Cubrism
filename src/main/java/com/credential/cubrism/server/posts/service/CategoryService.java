package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.posts.dto.CategoryListGetDTO;
import com.credential.cubrism.server.posts.entity.Category;
import com.credential.cubrism.server.posts.repository.CategoryRepository;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final QualificationListRepository qualificationListRepository;

    @Transactional
    @CachePut(value = "categoryList")
    public List<CategoryListGetDTO> saveCategory() {
        List<Category> savedCategories = categoryRepository.saveAll(qualificationListRepository.findAll().stream()
                .map(qualificationList -> {
                    Category category = new Category();
                    category.setCode(qualificationList.getCode());
                    category.setCategory(qualificationList.getName());
                    return category;
                })
                .collect(Collectors.toList()));

        return savedCategories.stream()
                .map(category -> new CategoryListGetDTO(category.getCode(), category.getCategory()))
                .collect(Collectors.toList());
    }

    @Cacheable("categoryList")
    public List<CategoryListGetDTO> categoryList(String search) {
        List<Category> categories;

        if (search == null || search.isEmpty()) {
            categories = categoryRepository.findAll();
        } else {
            categories = categoryRepository.findByCategoryName(search.replaceAll("\\s", ""));
        }

        if (categories.isEmpty()) {
            throw new IllegalArgumentException("해당 카테고리가 없습니다.");
        }

        return categories.stream()
                .map(category -> new CategoryListGetDTO(category.getCode(), category.getCategory()))
                .collect(Collectors.toList());
    }
}
