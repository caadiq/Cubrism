package com.credential.cubrism.server.qualification.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "ExamStandards")
public class ExamStandards {
    @Id
    @Column(name = "file_id", nullable = false)
    private UUID fileId;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code")
    private QualificationDetails qualificationDetails;
}
