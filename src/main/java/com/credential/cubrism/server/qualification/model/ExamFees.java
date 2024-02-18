package com.credential.cubrism.server.qualification.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ExamFees")
public class ExamFees {
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "written_fee")
    private Integer writtenFee;

    @Column(name = "practical_fee")
    private Integer practicalFee;

    @OneToOne
    @JoinColumn(name = "code", referencedColumnName = "code", insertable = false, updatable = false)
    private QualificationDetails qualificationDetails;
}