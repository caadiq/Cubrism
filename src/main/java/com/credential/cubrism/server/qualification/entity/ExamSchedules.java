package com.credential.cubrism.server.qualification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "ExamSchedules")
public class ExamSchedules {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    @Column(name = "category")
    private String category;

    @Column(name = "written_app")
    private String writtenApp;

    @Column(name = "written_exam")
    private String writtenExam;

    @Column(name = "written_exam_result")
    private String writtenExamResult;

    @Column(name = "practical_app")
    private String practicalApp;

    @Column(name = "practical_exam")
    private String practicalExam;

    @Column(name = "practical_exam_result")
    private String practicalExamResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code")
    private QualificationDetails qualificationDetails;
}