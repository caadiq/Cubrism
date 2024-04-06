package com.credential.cubrism.server.studygroup.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.studygroup.dto.*;
import com.credential.cubrism.server.studygroup.entity.*;
import com.credential.cubrism.server.studygroup.repository.GroupMembersRepository;
import com.credential.cubrism.server.studygroup.repository.PendingMembersRepository;
import com.credential.cubrism.server.studygroup.repository.StudyGroupGoalRepository;
import com.credential.cubrism.server.studygroup.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final PendingMembersRepository pendingMembersRepository;
    private final StudyGroupGoalRepository studyGroupGoalRepository;

    private final SecurityUtil securityUtil;

    // 스터디 그룹 생성
    @Transactional
    public ResponseEntity<MessageDto> createStudyGroup(StudyGroupCreateDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        StudyGroup studyGroup = new StudyGroup();
        studyGroup.setGroupName(dto.getGroupName());
        studyGroup.setGroupDescription(dto.getGroupDescription());
        studyGroup.setMaxMembers(dto.getMaxMembers());

        List<GroupTags> groupTagsList = dto.getTags().stream()
                .map(tag -> {
                    GroupTags groupTags = new GroupTags();
                    groupTags.setStudyGroup(studyGroup);
                    groupTags.setTagName(tag);
                    return groupTags;
                }).toList();
        studyGroup.setGroupTags(groupTagsList);
        studyGroup.setHidden(false);

        GroupMembers groupMembers = new GroupMembers();
        groupMembers.setUser(currentUser);
        groupMembers.setStudyGroup(studyGroup);
        groupMembers.setAdmin(true);
        studyGroup.setGroupMembers(List.of(groupMembers));

        studyGroupRepository.save(studyGroup);

        return ResponseEntity.ok().body(new MessageDto("스터디 그룹을 생성했습니다."));
    }

    // 스터디 그룹 가입 요청
    public ResponseEntity<MessageDto> joinStudyGroup(Long groupId) {
        Users currentUser = securityUtil.getCurrentUser();

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        if (studyGroup.getGroupMembers().stream().anyMatch(groupMembers -> groupMembers.getUser().getUuid().equals(currentUser.getUuid()))) {
            throw new CustomException(ErrorCode.STUDY_GROUP_ALREADY_JOINED);
        }

        PendingMembers pendingMembers = new PendingMembers();
        pendingMembers.setUser(currentUser);
        pendingMembers.setStudyGroup(studyGroup);
        pendingMembersRepository.save(pendingMembers);

        return ResponseEntity.ok().body(new MessageDto("스터디 그룹 가입을 요청했습니다."));
    }

    // 스터디 그룹 가입 요청 승인
    public ResponseEntity<MessageDto> approveJoinRequest(UUID memberId) {
        Users currentUser = securityUtil.getCurrentUser();

        PendingMembers pendingMember = pendingMembersRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.PENDING_MEMBER_NOT_FOUND));

        groupMembersRepository.findByUserAndStudyGroupAndAdmin(currentUser, pendingMember.getStudyGroup(), true)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_ADMIN));

        GroupMembers newMember = new GroupMembers();
        newMember.setUser(pendingMember.getUser());
        newMember.setStudyGroup(pendingMember.getStudyGroup());
        newMember.setAdmin(false);

        groupMembersRepository.save(newMember);
        pendingMembersRepository.delete(pendingMember);

        return ResponseEntity.ok().body(new MessageDto("스터디 그룹 가입을 승인했습니다."));
    }

    // 가입 요청 목록
    public List<StudyGroupJoinRequestDto> getAllJoinRequests() {
        Users currentUser = securityUtil.getCurrentUser();

        return groupMembersRepository.findAllByUserAndAdmin(currentUser, true).stream()
                .flatMap(groupMembers -> pendingMembersRepository.findByStudyGroup(groupMembers.getStudyGroup()).stream())
                .map(request -> new StudyGroupJoinRequestDto(
                        request.getMemberId(),
                        request.getStudyGroup().getGroupId(),
                        request.getStudyGroup().getGroupName(),
                        request.getUser().getNickname(),
                        request.getRequestDate()
                ))
                .collect(Collectors.toList());
    }

    // 스터디 그룹 탈퇴
    public ResponseEntity<MessageDto> leaveStudyGroup(Long groupId) {
        Users currentUser = securityUtil.getCurrentUser();

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        GroupMembers groupMembers = groupMembersRepository.findByUserAndStudyGroup(currentUser, studyGroup)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_MEMBER));

        if (groupMembers.isAdmin()) {
            throw new CustomException(ErrorCode.STUDY_GROUP_ADMIN_LEAVE);
        }

        groupMembersRepository.delete(groupMembers);

        return ResponseEntity.ok().body(new MessageDto("스터디 그룹을 탈퇴했습니다."));
    }

    // 스터디 그룹 삭제
    public ResponseEntity<MessageDto> deleteStudyGroup(Long groupId) {
        Users currentUser = securityUtil.getCurrentUser();

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        GroupMembers groupMembers = groupMembersRepository.findByUserAndStudyGroup(currentUser, studyGroup)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_MEMBER));

        if (!groupMembers.isAdmin()) {
            throw new CustomException(ErrorCode.STUDY_GROUP_NOT_ADMIN);
        }

        studyGroupRepository.delete(studyGroup);

        return ResponseEntity.ok().body(new MessageDto("스터디 그룹을 삭제했습니다."));
    }

    // 스터디 그룹 목록
    public ResponseEntity<StudyGroupListDto> studyGroupList(Pageable pageable, boolean recruiting) {
        Page<StudyGroup> studyGroup = studyGroupRepository.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(getStudyGroupList(studyGroup, pageable, recruiting));
    }

    // 내 스터디 그룹 목록
    public ResponseEntity<StudyGroupListDto> myStudyGroupList(Pageable pageable, boolean recruiting) {
        Users currentUser = securityUtil.getCurrentUser();
        Page<StudyGroup> studyGroup = studyGroupRepository.findByUserId(currentUser.getUuid(), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(getStudyGroupList(studyGroup, pageable, recruiting));
    }

    private StudyGroupListDto getStudyGroupList(Page<StudyGroup> studyGroup, Pageable pageable, boolean recruiting) {
        return new StudyGroupListDto(
                new StudyGroupListDto.Pageable(
                        studyGroup.hasPrevious() ? pageable.getPageNumber() - 1 : null,
                        pageable.getPageNumber(),
                        studyGroup.hasNext() ? pageable.getPageNumber() + 1 : null
                ),
                studyGroup.stream()
                        .filter(group -> !recruiting || group.getGroupMembers().size() < group.getMaxMembers())
                        .map(group -> {
                            boolean isRecruiting = group.getGroupMembers().size() < group.getMaxMembers(); // 모집중 여부
                            return new StudyGroupListDto.StudyGroupList(
                                    group.getGroupId(),
                                    group.getGroupName(),
                                    group.getGroupDescription(),
                                    group.getGroupMembers().size(),
                                    group.getMaxMembers(),
                                    isRecruiting,
                                    group.getGroupTags().stream()
                                            .map(GroupTags::getTagName)
                                            .toList()
                            );
                        }).toList());
    }

    // 스터디 그룹 정보
    public ResponseEntity<StudyGroupInfoDto> studyGroupInfo(Long groupId) {
        return studyGroupRepository.findById(groupId)
                .map(group -> {
                    boolean isRecruiting = group.getGroupMembers().size() < group.getMaxMembers();
                    return ResponseEntity.ok().body(new StudyGroupInfoDto(
                            group.getGroupId(),
                            group.getGroupName(),
                            group.getGroupDescription(),
                            group.getGroupMembers().stream()
                                    .filter(GroupMembers::isAdmin)
                                    .map(groupMembers -> groupMembers.getUser().getNickname())
                                    .findFirst()
                                    .orElse(null),
                            group.getGroupMembers().stream()
                                    .filter(GroupMembers::isAdmin)
                                    .map(groupMembers -> groupMembers.getUser().getImageUrl())
                                    .findFirst()
                                    .orElse(null),
                            group.getGroupMembers().size(),
                            group.getMaxMembers(),
                            group.getGroupTags().stream()
                                    .map(GroupTags::getTagName)
                                    .toList(),
                            isRecruiting
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // 스터디 그룹 목표 추가
    public ResponseEntity<MessageDto> addStudyGroupGoal(StudyGroupGoalCreateDto dto) {
        Long groupId = dto.getStudyGroupId();
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid study group Id:" + groupId));

        StudyGroupGoal goal = new StudyGroupGoal();
        goal.setGoalName(dto.getGoalName());

        // details 필드 설정 부분 수정
        List<GoalDetail> details = dto.getDetails().stream()
                .map(detailStr -> {
                    GoalDetail detail = new GoalDetail();
                    detail.setDetail(detailStr);
                    detail.setStudyGroupGoal(goal);
                    return detail;
                }).collect(Collectors.toList());
        goal.setDetails(details);

        goal.setStudyGroup(studyGroup);
        studyGroup.getStudyGroupGoals().add(goal);

        studyGroupGoalRepository.save(goal);
        studyGroupRepository.save(studyGroup);

        return ResponseEntity.ok().body(new MessageDto("스터디 그룹에 목표를 추가했습니다."));
    }

    // 스터디 그룹 목표 삭제
    public ResponseEntity<MessageDto> deleteGoalFromStudyGroup(Long goalId) {
        StudyGroupGoal goal = studyGroupGoalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid goal Id:" + goalId));

        studyGroupGoalRepository.delete(goal);

        return ResponseEntity.ok().body(new MessageDto("스터디 그룹 목표를 삭제했습니다."));
    }

}
