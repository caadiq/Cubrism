package com.credential.cubrism.server.studygroup.entity;

import com.credential.cubrism.server.authentication.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.List;

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

    @ManyToMany(mappedBy = "completedGoals")
    private List<UserGoal> completedUserGoals;

    @ManyToMany(mappedBy = "uncompletedGoals")
    private List<UserGoal> uncompletedUserGoals;
}