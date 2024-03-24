package com.credential.cubrism.server.studygroup.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.studygroup.dto.JoinRequestDto;
import com.credential.cubrism.server.studygroup.dto.StudyGroupCreateDto;
import com.credential.cubrism.server.studygroup.dto.StudyGroupListDto;
import com.credential.cubrism.server.studygroup.entity.GroupMembers;
import com.credential.cubrism.server.studygroup.entity.GroupTags;
import com.credential.cubrism.server.studygroup.entity.PendingMembers;
import com.credential.cubrism.server.studygroup.entity.StudyGroup;
import com.credential.cubrism.server.studygroup.repository.GroupMembersRepository;
import com.credential.cubrism.server.studygroup.repository.PendingMembersRepository;
import com.credential.cubrism.server.studygroup.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final PendingMembersRepository pendingMembersRepository;

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

        GroupMembers groupMembers = new GroupMembers();
        groupMembers.setUser(currentUser);
        groupMembers.setStudyGroup(studyGroup);
        groupMembers.setAdmin(true);
        studyGroup.setGroupMembers(List.of(groupMembers));

        studyGroupRepository.save(studyGroup);

        return ResponseEntity.ok().body(new MessageDto("스터디 그룹을 생성했습니다."));
    }

    // 스터디 그룹 가입
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

        // Check if the current user is an admin of the study group
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

    public List<JoinRequestDto> getAllJoinRequests() {
        Users currentUser = securityUtil.getCurrentUser();

        List<GroupMembers> adminGroups = groupMembersRepository.findAllByUserAndAdmin(currentUser, true);

        List<JoinRequestDto> allJoinRequests = new ArrayList<>();

        for (GroupMembers groupMembers : adminGroups) {
            StudyGroup studyGroup = groupMembers.getStudyGroup();
            List<PendingMembers> joinRequests = pendingMembersRepository.findByStudyGroup(studyGroup);

            for (PendingMembers request : joinRequests) {
                JoinRequestDto joinRequestDto = new JoinRequestDto();
                joinRequestDto.setMemberId(request.getMemberId());
                joinRequestDto.setGroupId(studyGroup.getGroupId());
                joinRequestDto.setGroupName(studyGroup.getGroupName());
                joinRequestDto.setUserName(request.getUser().getNickname());
                joinRequestDto.setRequestDate(request.getRequestDate());

                allJoinRequests.add(joinRequestDto);
            }
        }

        return allJoinRequests;
    }




    // 자정마다 만료된 가입 신청 삭제
//    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
//    public void rejectExpiredJoinRequests() {
//        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
//        List<PendingMembers> expiredRequests = pendingMembersRepository.findAllByRequestDateBefore(oneWeekAgo);
//        pendingMembersRepository.deleteAll(expiredRequests);
//    }


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
    public ResponseEntity<StudyGroupListDto> studyGroupList(Pageable pageable) {
        Page<StudyGroup> studyGroup = studyGroupRepository.findAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(getStudyGroupList(studyGroup, pageable));
    }

    // 내 스터디 그룹 목록
    public ResponseEntity<StudyGroupListDto> myStudyGroupList(Pageable pageable) {
        Users currentUser = securityUtil.getCurrentUser();
        Page<StudyGroup> studyGroup = studyGroupRepository.findByUserId(currentUser.getUuid(), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(getStudyGroupList(studyGroup, pageable));
    }

    private StudyGroupListDto getStudyGroupList(Page<StudyGroup> studyGroup, Pageable pageable) {
        return new StudyGroupListDto(
                new StudyGroupListDto.Pageable(
                        studyGroup.hasPrevious() ? pageable.getPageNumber() - 1 : null,
                        pageable.getPageNumber(),
                        studyGroup.hasNext() ? pageable.getPageNumber() + 1 : null
                ),
                studyGroup.stream()
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

}
