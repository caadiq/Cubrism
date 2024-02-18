package com.credential.cubrism.server.qualification.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "QualificationList")
public class QualificationList {
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "middle_field_name")
    private String middleFieldName;

    @Column(name = "major_field_name")
    private String majorFieldName;

    @Column(name = "qual_name")
    private String qualName;

    @Column(name = "series_name")
    private String seriesName;
}
