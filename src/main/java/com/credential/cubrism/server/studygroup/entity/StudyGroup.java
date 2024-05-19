package com.credential.cubrism.server.studygroup.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "StudyGroup")
public class StudyGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "group_description")
    private String groupDescription;

    @Column(name = "max_members")
    private int maxMembers;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL)
    private List<GroupMembers> groupMembers;

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL)
    private List<GroupTags> groupTags;

    @OneToOne(mappedBy = "studyGroup", cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id")
    private StudyGroupDDay dDay;

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL)
    private List<StudyGroupGoal> studyGroupGoals;

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL)
    private List<StudyGroupGoalSubmit> studyGroupGoalSubmit;
}
