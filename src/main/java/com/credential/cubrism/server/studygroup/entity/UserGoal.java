package com.credential.cubrism.server.studygroup.entity;

import com.credential.cubrism.server.authentication.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @JoinColumn(name = "goal_id")
    private StudyGroupGoal studyGroupGoal;

    @ElementCollection
    @CollectionTable(name = "completed_details", joinColumns = @JoinColumn(name = "user_goal_id"))
    @Column(name = "detail")
    private List<String> completedDetails;
}
