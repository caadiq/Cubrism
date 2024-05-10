package com.credential.cubrism.server.studygroup.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "StudyGroupGoal")
public class StudyGroupGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long goalId;

    @Column(name = "goal_name")
    private String goalName;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private StudyGroup studyGroup;
}