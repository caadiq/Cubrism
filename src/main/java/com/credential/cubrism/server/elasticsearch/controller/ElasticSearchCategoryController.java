package com.credential.cubrism.server.elasticsearch.controller;

import com.credential.cubrism.server.elasticsearch.domain.ElasticSearchCategory;
import com.credential.cubrism.server.elasticsearch.service.ElasticSearchCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ElasticSearchCategoryController {
    private final ElasticSearchCategoryService elasticSearchCategoryService;

    @GetMapping("/test/category_elastic")
    public List<ElasticSearchCategory> getAllQualifications() {
        return elasticSearchCategoryService.getAllQualifications();
    }
}