package com.credential.cubrism.server.elasticsearch.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Document(indexName = "category")
public class ElasticSearchCategory {
    @Id
    @Field(type = FieldType.Text)
    private String code;

    @Field(type = FieldType.Text)
    private String name;
}
