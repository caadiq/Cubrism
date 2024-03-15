package com.credential.cubrism.server.posts.entity;

import com.credential.cubrism.server.qualification.model.QualificationList;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Category")
public class Category {
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "category", nullable = false)
    private String category;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code", nullable = false)
    private QualificationList qualificationList;
}
