package com.credential.cubrism.server.posts.repository;

import com.credential.cubrism.server.posts.entity.ElasticSearchCategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticSearchCategoryRepository extends ElasticsearchRepository<ElasticSearchCategory, String> {

}