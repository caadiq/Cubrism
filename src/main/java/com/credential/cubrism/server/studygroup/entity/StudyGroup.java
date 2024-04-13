package com.credential.cubrism.server.studygroup.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Column(name = "d_day", nullable = true)
    private LocalDate dDay;

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL)
    private List<StudyGroupGoal> studyGroupGoals;

    @Column(name = "hidden", nullable = true)
    private boolean hidden;

    public long calculateDDay() {
        return ChronoUnit.DAYS.between(LocalDate.now(), this.dDay);
    }

    public int getTotalGoals() {
        return studyGroupGoals.size();
    }


}
