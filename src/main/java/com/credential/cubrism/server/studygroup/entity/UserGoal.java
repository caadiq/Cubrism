package com.credential.cubrism.server.studygroup.entity;

import com.credential.cubrism.server.authentication.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "UserGoal")
public class UserGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_goal_id")
    private Long userGoalId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private StudyGroup studyGroup;

    @ManyToMany
    @JoinTable(
            name = "UserGoalCompleted",
            joinColumns = @JoinColumn(name = "user_goal_id"),
            inverseJoinColumns = @JoinColumn(name = "goal_id")
    )
    private List<StudyGroupGoal> completedGoals = new ArrayList<>();;

    @ManyToMany
    @JoinTable(
            name = "UserGoalUncompleted",
            joinColumns = @JoinColumn(name = "user_goal_id"),
            inverseJoinColumns = @JoinColumn(name = "goal_id")
    )
    private List<StudyGroupGoal> uncompletedGoals = new ArrayList<>();; // 추가된 필드

}
