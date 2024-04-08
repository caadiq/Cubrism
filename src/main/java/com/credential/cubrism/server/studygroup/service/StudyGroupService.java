package com.credential.cubrism.server.studygroup.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.studygroup.dto.*;
import com.credential.cubrism.server.studygroup.entity.*;
import com.credential.cubrism.server.studygroup.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final PendingMembersRepository pendingMembersRepository;
    private final StudyGroupGoalRepository studyGroupGoalRepository;
    private final UserGoalRepository userGoalRepository;
    private final UserRepository userRepository;
    private final GoalDetailRepository goalDetailRepository;

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

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDto("스터디 그룹을 생성했습니다."));
    }

    // 스터디 그룹 가입 요청
    public ResponseEntity<MessageDto> requestJoin(Long groupId) {
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

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("스터디 그룹 가입을 요청했습니다."));
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

        // 스터디 그룹의 모든 목표에 대해 새로운 멤버에 대한 UserGoal 인스턴스를 생성하고 저장
        for (StudyGroupGoal goal : newMember.getStudyGroup().getStudyGroupGoals()) {
            UserGoal userGoal = new UserGoal();
            userGoal.setUser(newMember.getUser());
            userGoal.setStudyGroupGoal(goal);
            userGoal.setCompletedDetails(new ArrayList<>());
            userGoalRepository.save(userGoal);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("스터디 그룹 가입을 승인했습니다."));
    }

    // 가입 요청 목록
    public ResponseEntity<List<StudyGroupJoinListDto>> getJoinRequest() {
        Users currentUser = securityUtil.getCurrentUser();

        List<StudyGroupJoinListDto> joinList = groupMembersRepository.findByUserAndAdmin(currentUser, true).stream()
                .flatMap(groupMembers -> pendingMembersRepository.findByStudyGroup(groupMembers.getStudyGroup()).stream()
                        .map(pendingMembers -> new StudyGroupJoinListDto(
                                pendingMembers.getMemberId(),
                                pendingMembers.getUser().getNickname(),
                                pendingMembers.getUser().getEmail(),
                                pendingMembers.getRequestDate()
                        )))
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(joinList);
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

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("스터디 그룹을 탈퇴했습니다."));
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

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("스터디 그룹을 삭제했습니다."));
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
                                    .map(groupMembers -> Optional.ofNullable(groupMembers.getUser().getImageUrl()))
                                    .findFirst()
                                    .orElse(Optional.empty())
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
    @Transactional
    public ResponseEntity<MessageDto> addStudyGroupGoal(StudyGroupAddGoalDto dto) {
        Long groupId = dto.getStudyGroupId();
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

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

        // 스터디 그룹의 모든 멤버들에게 UserGoal 인스턴스를 생성하고 저장
        for (GroupMembers member : studyGroup.getGroupMembers()) {
            UserGoal userGoal = new UserGoal();
            userGoal.setUser(member.getUser());
            userGoal.setStudyGroupGoal(goal);
            userGoal.setCompletedDetails(new ArrayList<>());
            userGoalRepository.save(userGoal);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDto("스터디 그룹에 목표를 추가했습니다."));
    }

    // 스터디 그룹 목표 삭제
    public ResponseEntity<MessageDto> deleteStudyGroupGoal(Long goalId) {
        StudyGroupGoal goal = studyGroupGoalRepository.findById(goalId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_GOAL_NOT_FOUND));

        studyGroupGoalRepository.delete(goal);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("스터디 그룹 목표를 삭제했습니다."));
    }

    // 스터디 그룹 목표 수정
    public ResponseEntity<MessageDto> updateStudyGroupGoal(StudyGroupUpdateGoalDto dto) {
        StudyGroupGoal goal = studyGroupGoalRepository.findById(dto.getGoalId())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_GOAL_NOT_FOUND));

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

        studyGroupGoalRepository.save(goal);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("스터디 그룹 목표를 수정했습니다."));
    }

    public ResponseEntity<List<UserGoalStatusDto>> getUserGoals(Long groupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        List<UserGoalStatusDto> userGoalStatusList = studyGroup.getGroupMembers().stream()
                .map(member -> {
                    UserGoal userGoal = userGoalRepository.findByUserAndStudyGroupGoal_StudyGroup(member.getUser(), studyGroup)
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_GOAL_NOT_FOUND));
                    List<GoalDetailDto> completedDetails = userGoal.getCompletedDetails().stream()
                            .map(detail -> new GoalDetailDto(detail.getId(), detail.getDetail()))
                            .collect(Collectors.toList());
                    List<GoalDetailDto> uncompletedDetails = userGoal.getStudyGroupGoal().getDetails().stream()
                            .filter(detail -> !completedDetails.contains(new GoalDetailDto(detail.getId(), detail.getDetail())))
                            .map(detail -> new GoalDetailDto(detail.getId(), detail.getDetail()))
                            .collect(Collectors.toList());
                    double completionPercentage = userGoal.getCompletionPercentage();
                    return new UserGoalStatusDto(member.getUser().getNickname(), completedDetails, uncompletedDetails, completionPercentage);
                })
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(userGoalStatusList);
    }
    @Transactional
    public ResponseEntity<MessageDto> completeGoalDetail(Long detailId) {
        Users currentUser = securityUtil.getCurrentUser();
        GoalDetail detail = goalDetailRepository.findById(detailId)
                .orElseThrow(() -> new CustomException(ErrorCode.GOAL_DETAIL_NOT_FOUND));

        UserGoal userGoal = userGoalRepository.findByUserAndStudyGroupGoal_StudyGroup(currentUser, detail.getStudyGroupGoal().getStudyGroup())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_GOAL_NOT_FOUND));

        if (!userGoal.getCompletedDetails().contains(detail)) {
            userGoal.getCompletedDetails().add(detail);
            userGoalRepository.save(userGoal);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("세부사항을 완료했습니다."));
    }




}
