package com.credential.cubrism.server.studygroup.entity;

import com.credential.cubrism.server.authentication.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "StudyGroupGoalSubmit")
public class StudyGroupGoalSubmit {
    @Id
    @Column(name = "user_goal_id", nullable = false)
    private Long userGoalId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @CreatedDate
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_goal_id")
    @MapsId
    private UserGoal userGoal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private StudyGroup studyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
}
