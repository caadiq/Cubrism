package com.credential.cubrism.server.studygroup.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "goalDetails")
public class GoalDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "detail")
    private String detail;

    @ManyToOne
    @JoinColumn(name = "goal_id")
    private StudyGroupGoal studyGroupGoal;

    @ManyToMany(mappedBy = "completedDetails")
    private List<UserGoal> completedUserGoals;
}
