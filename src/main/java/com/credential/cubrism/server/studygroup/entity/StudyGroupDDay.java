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
    @OneToOne
    @JoinColumn(name = "group_id")
    @MapsId
    private StudyGroup studyGroup;

    @Column(name = "d_day")
    private LocalDate dDay;

    @Column(name = "d_title")
    private String dTitle;
}
