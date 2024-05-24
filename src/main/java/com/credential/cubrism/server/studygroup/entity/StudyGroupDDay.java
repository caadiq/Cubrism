package com.credential.cubrism.server.studygroup.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "StudyGroupDDay")
public class StudyGroupDDay {
    @Id
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "d_day", nullable = false)
    private LocalDate dDay;

    @Column(name = "d_title", nullable = false)
    private String dTitle;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @MapsId
    private StudyGroup studyGroup;
}
