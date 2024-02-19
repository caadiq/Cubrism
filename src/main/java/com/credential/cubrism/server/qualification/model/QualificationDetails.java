package com.credential.cubrism.server.qualification.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "QualificationDetails")
public class QualificationDetails {
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "tendency")
    private String tendency;

    @Column(name = "acquisition")
    private String acquisition;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code")
    private QualificationList qualificationList;

    @OneToMany(mappedBy = "qualificationDetails", cascade = CascadeType.ALL)
    private List<ExamSchedules> examSchedules;

    @OneToOne(mappedBy = "qualificationDetails", cascade = CascadeType.ALL)
    private ExamFees examFees;

    @OneToMany(mappedBy = "qualificationDetails", cascade = CascadeType.ALL)
    private List<ExamStandards> examStandards;

    @OneToMany(mappedBy = "qualificationDetails", cascade = CascadeType.ALL)
    private List<PublicQuestions> publicQuestions;

    @OneToMany(mappedBy = "qualificationDetails", cascade = CascadeType.ALL)
    private List<RecommendBooks> recommendBooks;
}
