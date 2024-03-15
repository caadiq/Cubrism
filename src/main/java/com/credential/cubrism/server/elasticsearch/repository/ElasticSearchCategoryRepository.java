package com.credential.cubrism.server.elasticsearch.repository;


import com.credential.cubrism.server.elasticsearch.domain.ElasticSearchCategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticSearchCategoryRepository extends ElasticsearchRepository<ElasticSearchCategory, String> {

}