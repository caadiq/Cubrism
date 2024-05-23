package com.credential.cubrism.server.studygroup.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.notification.entity.FcmTokens;
import com.credential.cubrism.server.notification.repository.FcmRepository;
import com.credential.cubrism.server.notification.utils.FcmUtils;
import com.credential.cubrism.server.s3.utils.S3Util;
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

import java.time.LocalDate;
import java.util.Comparator;
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
    private final StudyGroupGoalSubmitRepository studyGroupGoalSubmitRepository;
    private final UserGoalRepository userGoalRepository;
    private final FcmRepository fcmRepository;

    private final SecurityUtil securityUtil;
    private final FcmUtils fcmUtils;
    private final S3Util s3Util;

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

        GroupMembers groupMembers = new GroupMembers();
        groupMembers.setUser(currentUser);
        groupMembers.setStudyGroup(studyGroup);
        groupMembers.setAdmin(true);
        studyGroup.setGroupMembers(List.of(groupMembers));

        studyGroupRepository.save(studyGroup);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDto("스터디 그룹을 생성했습니다."));
    }

    // 스터디 그룹 삭제
    @Transactional
    public ResponseEntity<MessageDto> deleteStudyGroup(Long groupId) {
        Users currentUser = securityUtil.getCurrentUser();

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        GroupMembers groupMembers = groupMembersRepository.findByUserAndStudyGroup(currentUser, studyGroup)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_MEMBER));

        if (!groupMembers.isAdmin()) {
            throw new CustomException(ErrorCode.STUDY_GROUP_NOT_ADMIN);
        }

        // 해당 스터디 그룹에 속한 GroupMembers를 모두 찾아서 삭제
        List<GroupMembers> members = groupMembersRepository.findByStudyGroup(studyGroup);
        groupMembersRepository.deleteAll(members);

        // 스터디 그룹과 관련된 UserGoal을 찾아 삭제
        List<UserGoal> userGoals = userGoalRepository.findByStudyGroup(studyGroup);
        userGoalRepository.deleteAll(userGoals);

        // 스터디 그룹과 관련된 StudyGroupGoal을 찾아 삭제
        List<StudyGroupGoal> studyGroupGoals = studyGroupGoalRepository.findByStudyGroup(studyGroup);
        studyGroupGoalRepository.deleteAll(studyGroupGoals);

        // 스터디 그룹과 관련된 StudyGroupGoalSubmit을 찾아 삭제
        List<StudyGroupGoalSubmit> studyGroupGoalSubmits = studyGroupGoalSubmitRepository.findByStudyGroup(studyGroup);
        studyGroupGoalSubmitRepository.deleteAll(studyGroupGoalSubmits);

        studyGroupRepository.delete(studyGroup);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("스터디 그룹을 삭제했습니다."));
    }

    // 스터디 그룹 목록
    public ResponseEntity<StudyGroupListDto> studyGroupList(Pageable pageable, boolean recruiting) {
        Page<StudyGroup> studyGroup = studyGroupRepository.findAll(pageable);

        StudyGroupListDto studyGroupListDto = new StudyGroupListDto(
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

        return ResponseEntity.status(HttpStatus.OK).body(studyGroupListDto);
    }

    // 내가 가입한 스터디 그룹 목록
    public ResponseEntity<List<StudyGroupListDto.StudyGroupList>> myStudyGroupList() {
        Users currentUser = securityUtil.getCurrentUser();

        List<StudyGroupListDto.StudyGroupList> studyGroupList = studyGroupRepository.findAllByUserId(currentUser.getUuid())
                .stream()
                .map(group -> {
                    boolean isRecruiting = group.getGroupMembers().size() < group.getMaxMembers();
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
                })
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(studyGroupList);
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
                            isRecruiting,
                            group.getGroupMembers().stream()
                                    .map(groupMembers -> groupMembers.getUser().getEmail())
                                    .toList()
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // 스터디 그룹 가입 신청
    @Transactional
    public ResponseEntity<MessageDto> requestJoin(Long groupId) {
        Users currentUser = securityUtil.getCurrentUser();

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        if (studyGroup.getGroupMembers().stream().anyMatch(groupMembers -> groupMembers.getUser().getUuid().equals(currentUser.getUuid()))) {
            throw new CustomException(ErrorCode.STUDY_GROUP_ALREADY_JOINED);
        }

        if (pendingMembersRepository.findByUserAndStudyGroup(currentUser, studyGroup).isPresent()) {
            throw new CustomException(ErrorCode.STUDY_GROUP_ALREADY_REQUESTED);
        }

        PendingMembers pendingMembers = new PendingMembers();
        pendingMembers.setUser(currentUser);
        pendingMembers.setStudyGroup(studyGroup);
        pendingMembersRepository.save(pendingMembers);

        // 스터디 그룹 관리자에게 알림 전송
        GroupMembers admin = studyGroup.getGroupMembers().stream()
                .filter(GroupMembers::isAdmin)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_ADMIN));

        String fcmToken = fcmRepository.findByUserId(admin.getUser().getUuid())
                .map(FcmTokens::getToken)
                .orElse(null);

        // FCM 토큰이 존재하는 경우 알림 전송
        if (fcmToken != null) {
            // 알림 메시지
            String title = "스터디 그룹 가입 요청이 있습니다.";
            String body = currentUser.getNickname() + "님이 가입을 요청했습니다.";
            String type = "STUDY|" + studyGroup.getGroupId();

            // 알림 전송
            fcmUtils.sendMessageTo(fcmToken, title, body, type);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("스터디 그룹 가입을 요청했습니다."));
    }

    // 스터디 그룹 가입 신청 취소
    @Transactional
    public ResponseEntity<MessageDto> cancelJoinRequest(UUID memberId) {
        Users currentUser = securityUtil.getCurrentUser();

        PendingMembers pendingMember = pendingMembersRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.PENDING_MEMBER_NOT_FOUND));

        if (!pendingMember.getUser().getUuid().equals(currentUser.getUuid())) {
            throw new CustomException(ErrorCode.PENDING_MEMBER_NOT_MATCH);
        }

        pendingMembersRepository.delete(pendingMember);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("스터디 그룹 가입 신청을 취소했습니다."));
    }

    // 가입 신청한 스터디 그룹 목록
    public ResponseEntity<List<StudyGroupJoinListDto>> joinRequestList() {
        Users currentUser = securityUtil.getCurrentUser();

        List<StudyGroupJoinListDto> joinList = pendingMembersRepository.findByUserOrderByRequestDateDesc(currentUser).stream()
                .map(pendingMembers -> new StudyGroupJoinListDto(
                        pendingMembers.getMemberId(),
                        pendingMembers.getStudyGroup().getGroupName(),
                        pendingMembers.getStudyGroup().getGroupDescription(),
                        pendingMembers.getStudyGroup().getGroupTags().stream()
                                .map(GroupTags::getTagName)
                                .toList(),
                        pendingMembers.getStudyGroup().getGroupMembers().stream()
                                .filter(GroupMembers::isAdmin)
                                .map(groupMembers -> groupMembers.getUser().getNickname())
                                .findFirst()
                                .orElse(null),
                        pendingMembers.getStudyGroup().getGroupMembers().stream()
                                .filter(GroupMembers::isAdmin)
                                .map(groupMembers -> Optional.ofNullable(groupMembers.getUser().getImageUrl()))
                                .findFirst()
                                .orElse(Optional.empty())
                                .orElse(null),
                        pendingMembers.getRequestDate().toString()
                ))
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(joinList);
    }

    // 스터디 그룹 가입 승인
    @Transactional
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

        // 새로 가입한 멤버에 대해서만 UserGoal 생성
        List<StudyGroupGoal> studyGroupGoals = newMember.getStudyGroup().getStudyGroupGoals();
        if (studyGroupGoals != null && !studyGroupGoals.isEmpty()) {
            for (StudyGroupGoal studyGroupGoal : studyGroupGoals) {
                UserGoal userGoal = new UserGoal();
                userGoal.setUser(newMember.getUser());
                userGoal.setStudyGroup(newMember.getStudyGroup());
                userGoal.setStudyGroupGoal(studyGroupGoal);
                userGoal.setCompleted(false); // completed 설정

                userGoalRepository.save(userGoal);
            }
        }

        // 가입 요청을 한 유저에게 알림 전송
        String fcmToken = fcmRepository.findByUserId(newMember.getUser().getUuid())
                .map(FcmTokens::getToken)
                .orElse(null);

        // FCM 토큰이 존재하는 경우 알림 전송
        if (fcmToken != null) {
            // 알림 메시지
            String title = "스터디 그룹 가입 승인";
            String body = "'" + newMember.getStudyGroup().getGroupName() + "' 스터디 그룹 가입이 승인되었습니다.";
            String type = "STUDY|" + newMember.getStudyGroup().getGroupId();

            // 알림 전송
            fcmUtils.sendMessageTo(fcmToken, title, body, type);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("스터디 그룹 가입을 승인했습니다."));
    }

    // 스터디 그룹 가입 거절
    @Transactional
    public ResponseEntity<MessageDto> denyJoinRequest(UUID memberId) {
        Users currentUser = securityUtil.getCurrentUser();

        PendingMembers pendingMember = pendingMembersRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.PENDING_MEMBER_NOT_FOUND));

        groupMembersRepository.findByUserAndStudyGroupAndAdmin(currentUser, pendingMember.getStudyGroup(), true)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_ADMIN));

        pendingMembersRepository.delete(pendingMember);

        // 가입 요청을 한 유저에게 알림 전송
        String fcmToken = fcmRepository.findByUserId(pendingMember.getUser().getUuid())
                .map(FcmTokens::getToken)
                .orElse(null);

        // FCM 토큰이 존재하는 경우 알림 전송
        if (fcmToken != null) {
            // 알림 메시지
            String title = "스터디 그룹 가입 거절";
            String body = "'" + pendingMember.getStudyGroup().getGroupName() + "' 스터디 그룹 가입이 거절되었습니다.";

            // 알림 전송
            fcmUtils.sendMessageTo(fcmToken, title, body, null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("스터디 그룹 가입을 거절했습니다."));
    }

    // 해당 스터디 그룹 가입 신청 목록
    public ResponseEntity<List<StudyGroupJoinRequestDto>> joinRequestList(Long groupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        Users currentUser = securityUtil.getCurrentUser();

        groupMembersRepository.findByUserAndStudyGroupAndAdmin(currentUser, studyGroup, true)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_ADMIN));

        List<StudyGroupJoinRequestDto> joinList = pendingMembersRepository.findByStudyGroup(studyGroup).stream()
                .map(pendingMembers -> new StudyGroupJoinRequestDto(
                        pendingMembers.getMemberId(),
                        pendingMembers.getStudyGroup().getGroupName(),
                        pendingMembers.getUser().getNickname(),
                        pendingMembers.getUser().getImageUrl(),
                        pendingMembers.getRequestDate()
                ))
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(joinList);
    }

    // 스터디 그룹 목표 추가
    @Transactional
    public ResponseEntity<MessageDto> addStudyGroupGoal(StudyGroupAddGoalDto dto) {
        Users currentUser = securityUtil.getCurrentUser();
        Long groupId = dto.getStudyGroupId();
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        groupMembersRepository.findByUserAndStudyGroupAndAdmin(currentUser, studyGroup, true)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_ADMIN));

        if (dDayPassed(studyGroup)) {
            throw new CustomException(ErrorCode.STUDY_GROUP_DDAY_PASSED);
        }

        StudyGroupGoal goal = new StudyGroupGoal();
        goal.setGoalName(dto.getGoalName());
        goal.setStudyGroup(studyGroup);
        studyGroup.getStudyGroupGoals().add(goal);

        studyGroupGoalRepository.save(goal);

        // 스터디 그룹의 모든 멤버들에게 UserGoal 인스턴스를 생성하고 저장
        for (GroupMembers member : studyGroup.getGroupMembers()) {
            UserGoal userGoal = new UserGoal();
            userGoal.setUser(member.getUser());
            userGoal.setStudyGroup(studyGroup);
            userGoal.setStudyGroupGoal(goal);
            userGoal.setCompleted(false);

            userGoalRepository.save(userGoal);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDto("목표를 추가했습니다."));
    }

    // 스터디 그룹 목표 삭제
    @Transactional
    public ResponseEntity<MessageDto> deleteStudyGroupGoal(Long goalId) {
        StudyGroupGoal goal = studyGroupGoalRepository.findById(goalId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_GOAL_NOT_FOUND));
        Users currentUser = securityUtil.getCurrentUser();
        StudyGroup studyGroup = goal.getStudyGroup();
        groupMembersRepository.findByUserAndStudyGroupAndAdmin(currentUser, studyGroup, true)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_ADMIN));
        if (dDayPassed(studyGroup)) {
            throw new CustomException(ErrorCode.STUDY_GROUP_DDAY_PASSED);
        }

        List<UserGoal> userGoals = userGoalRepository.findByStudyGroupGoal(goal);
        userGoalRepository.deleteAll(userGoals);

        studyGroupGoalRepository.delete(goal);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("목표를 삭제했습니다."));
    }

    // 스터디 그룹 목표 완료
    @Transactional
    public ResponseEntity<MessageDto> completeStudyGroupGoal(Long goalId) {
        Users currentUser = securityUtil.getCurrentUser();
        StudyGroupGoal goal = studyGroupGoalRepository.findById(goalId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_GOAL_NOT_FOUND));

        UserGoal userGoal = userGoalRepository.findByUserAndStudyGroupAndStudyGroupGoal(currentUser, goal.getStudyGroup(), goal)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_GOAL_NOT_FOUND));

        userGoal.setCompleted(true);
        userGoalRepository.save(userGoal);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("목표를 완료했습니다."));
    }


    public ResponseEntity<List<StudyGroupGoalDto>> getStudyGroupGoals(Long groupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        List<StudyGroupGoalDto> studyGroupGoalList = studyGroup.getStudyGroupGoals().stream()
                .map(goal -> new StudyGroupGoalDto(
                        studyGroup.getStudyGroupGoals().indexOf(goal) + 1,
                        goal.getGoalId(),
                        goal.getGoalName())
                )
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(studyGroupGoalList);
    }

    // 스터디 그룹 목표 제출
    @Transactional
    public ResponseEntity<MessageDto> submitStudyGroupGoal(StudyGroupGoalSubmitDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        StudyGroupGoal goal = studyGroupGoalRepository.findById(dto.getGoalId())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_GOAL_NOT_FOUND));

        UserGoal userGoal = userGoalRepository.findByUserAndStudyGroupGoal(currentUser, goal).orElseThrow(() -> new CustomException(ErrorCode.USER_GOAL_NOT_FOUND));

        StudyGroup studyGroup = studyGroupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        if(dDayPassed(studyGroup)){
            throw new CustomException(ErrorCode.STUDY_GROUP_DDAY_PASSED);
        }

        if (!userGoal.getUser().getUuid().equals(currentUser.getUuid())) {
            throw new CustomException(ErrorCode.USER_GOAL_NOT_MATCH);
        }

        // 목표가 이미 완료되었으면 제출 불가
        if (userGoal.isCompleted()) {
            throw new CustomException(ErrorCode.STUDY_GROUP_GOAL_ALREADY_COMPLETED);
        }

        //이미 제출중인 목표가 있으면 제출 불가
        if (studyGroupGoalSubmitRepository.findByUserGoal(userGoal).isPresent()) {
            throw new CustomException(ErrorCode.STUDY_GROUP_GOAL_ALREADY_SUBMITTED);
        }

        if (!s3Util.isFileExists(dto.getImageUrl())) {
            throw new CustomException(ErrorCode.S3_FILE_NOT_FOUND);
        }

        StudyGroupGoalSubmit studyGroupGoalSubmit = new StudyGroupGoalSubmit();
        studyGroupGoalSubmit.setUserGoal(userGoal);
        studyGroupGoalSubmit.setStudyGroup(studyGroup);
        studyGroupGoalSubmit.setUser(currentUser);
        studyGroupGoalSubmit.setContent(dto.getContent());
        studyGroupGoalSubmit.setImageUrl(dto.getImageUrl());
        studyGroupGoalSubmitRepository.save(studyGroupGoalSubmit);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("목표를 제출했습니다."));
    }

    // 스터디 그룹 목표 제출 목록
    public ResponseEntity<List<StudyGroupGoalSubmitListDto>> studyGroupGoalSubmitList(Long groupId) {
        Users currentUser = securityUtil.getCurrentUser();

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        groupMembersRepository.findByUserAndStudyGroupAndAdmin(currentUser, studyGroup, true)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_ADMIN));

        List<StudyGroupGoalSubmitListDto> submitList = studyGroupGoalSubmitRepository.findByUserGoal_StudyGroup_GroupId(groupId).stream()
                .map(submit -> new StudyGroupGoalSubmitListDto(
                        submit.getUserGoalId(),
                        submit.getUserGoal().getUser().getNickname(),
                        submit.getUser().getImageUrl(),
                        submit.getContent(),
                        submit.getImageUrl(),
                        submit.getSubmittedAt(),
                        submit.getUserGoal().getStudyGroupGoal().getGoalName()
                ))
                .collect(Collectors.toList());

        submitList.sort(Comparator.comparing(StudyGroupGoalSubmitListDto::getSubmittedAt).reversed());

        return ResponseEntity.status(HttpStatus.OK).body(submitList);
    }

    //스터디 그룹 목표 달성 인증 성공
    @Transactional(noRollbackFor = CustomException.class)
    public ResponseEntity<MessageDto> approveStudyGroupGoalSubmit(Long userGoalId) {
        Users currentUser = securityUtil.getCurrentUser();

        StudyGroupGoalSubmit studyGroupGoalSubmit = studyGroupGoalSubmitRepository.findById(userGoalId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_GOAL_NOT_FOUND));

        StudyGroup studyGroup = studyGroupGoalSubmit.getStudyGroup();

        groupMembersRepository.findByUserAndStudyGroupAndAdmin(currentUser, studyGroup, true)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_ADMIN));

        if(dDayPassed(studyGroup)){
            throw new CustomException(ErrorCode.STUDY_GROUP_DDAY_PASSED);
        }

        UserGoal userGoal = studyGroupGoalSubmit.getUserGoal();
        userGoal.setCompleted(true);
        userGoalRepository.save(userGoal);

        userGoal.setStudyGroupGoalSubmit(null);
        studyGroupGoalSubmitRepository.delete(studyGroupGoalSubmit);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("목표 달성 인증을 완료했습니다."));
    }

    //스터디 그룹 목표 달성 인증 거절
    @Transactional
    public ResponseEntity<MessageDto> denyStudyGroupGoalSubmit(Long userGoalId) {
        Users currentUser = securityUtil.getCurrentUser();

        StudyGroupGoalSubmit studyGroupGoalSubmit = studyGroupGoalSubmitRepository.findById(userGoalId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_GOAL_NOT_FOUND));

        StudyGroup studyGroup = studyGroupGoalSubmit.getStudyGroup();

        groupMembersRepository.findByUserAndStudyGroupAndAdmin(currentUser, studyGroup, true)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_ADMIN));
        if(dDayPassed(studyGroup)){
            throw new CustomException(ErrorCode.STUDY_GROUP_DDAY_PASSED);
        }

        studyGroupGoalSubmitRepository.delete(studyGroupGoalSubmit);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("목표 달성 인증을 거절했습니다."));
    }

    // 스터디 그룹 D-day 설정
    public ResponseEntity<MessageDto> setStudyGroupDDay(StudyGroupDDayDto dto) {
        Users currentUser = securityUtil.getCurrentUser();
        Long groupId = dto.getGroupId();
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        GroupMembers groupMembers = groupMembersRepository.findByUserAndStudyGroup(currentUser, studyGroup)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_MEMBER));

        if (!groupMembers.isAdmin()) {
            throw new CustomException(ErrorCode.STUDY_GROUP_NOT_ADMIN);
        }

        StudyGroupDDay studyGroupDDay = new StudyGroupDDay();
        studyGroupDDay.setDDay(dto.getDay());
        studyGroupDDay.setDTitle(dto.getTitle());
        studyGroupDDay.setStudyGroup(studyGroup);

        studyGroup.setDDay(studyGroupDDay);

        studyGroupRepository.save(studyGroup);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("D-Day를 설정했습니다."));
    }

    public ResponseEntity<StudyGroupDDayDto> getStudyGroupDDay(Long groupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        StudyGroupDDayDto studyGroupDDayDto = new StudyGroupDDayDto(
                studyGroup.getGroupId(),
                studyGroup.getDDay().getDTitle(),
                studyGroup.getDDay().getDDay()
        );

        return ResponseEntity.status(HttpStatus.OK).body(studyGroupDDayDto);
    }

    // 스터디 그룹 들어갔을 때 필요한 정보
    public ResponseEntity<StudyGroupEnterDto> getStudyGroupEnterInfo(Long groupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        List<StudyGroupMemberInfo> members = studyGroup.getGroupMembers().stream()
                .map(groupMembers -> {
                    Users user = groupMembers.getUser();
                    List<UserGoal> userGoals = userGoalRepository.findAllByUserAndStudyGroup(user, studyGroup);

                    List<StudyGroupGoalEnterDto> goals = userGoals.stream()
                            .map(userGoal -> {
                                boolean submitted = studyGroupGoalSubmitRepository.findByUserGoal_UserAndUserGoal_StudyGroupGoal(user, userGoal.getStudyGroupGoal()).isPresent();
                                return new StudyGroupGoalEnterDto(userGoal.getStudyGroupGoal().getGoalId(),
                                        userGoal.getStudyGroupGoal().getGoalName(),
                                        userGoal.isCompleted(),
                                        submitted
                                        );
                            })
                            .collect(Collectors.toList());

                    Double completionPercentage = userGoals.isEmpty() ? null : (double) userGoals.stream().filter(UserGoal::isCompleted).count() / userGoals.size() * 100;

                    UserGoalEnterDto userGoalEnterDto = new UserGoalEnterDto(goals, completionPercentage);

                    return new StudyGroupMemberInfo(user.getNickname(), user.getEmail(), user.getImageUrl(), groupMembers.isAdmin(), userGoalEnterDto);
                })
                .collect(Collectors.toList());

        StudyGroupDDayDto studyGroupDDayDto;
        if (studyGroup.getDDay() != null) {
            studyGroupDDayDto = new StudyGroupDDayDto(
                    studyGroup.getGroupId(),
                    studyGroup.getDDay().getDTitle(),
                    studyGroup.getDDay().getDDay()
            );
        } else {
            studyGroupDDayDto = new StudyGroupDDayDto();
            studyGroupDDayDto.setGroupId(studyGroup.getGroupId());
        }

        StudyGroupEnterDto studyGroupEnterDto = new StudyGroupEnterDto(studyGroup.getGroupName(), members, studyGroupDDayDto);

        return ResponseEntity.status(HttpStatus.OK).body(studyGroupEnterDto);
    }

    //스터디 그룹의 dday가 지났는지 확인
    public boolean dDayPassed(StudyGroup studyGroup) {
        LocalDate today = LocalDate.now();
        LocalDate dDay = studyGroup.getDDay().getDDay();

        return !dDay.isAfter(today);
    }
}
