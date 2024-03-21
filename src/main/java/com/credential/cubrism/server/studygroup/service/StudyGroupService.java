package com.credential.cubrism.server.studygroup.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.studygroup.dto.StudyGroupCreateDto;
import com.credential.cubrism.server.studygroup.dto.StudyGroupListDto;
import com.credential.cubrism.server.studygroup.entity.GroupMembers;
import com.credential.cubrism.server.studygroup.entity.GroupTags;
import com.credential.cubrism.server.studygroup.entity.StudyGroup;
import com.credential.cubrism.server.studygroup.repository.GroupMembersRepository;
import com.credential.cubrism.server.studygroup.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMembersRepository groupMembersRepository;

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
    // TODO : 가입 신청으로 바꿔야됨
    public ResponseEntity<MessageDto> joinStudyGroup(Long groupId) {
        Users currentUser = securityUtil.getCurrentUser();

        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        if (studyGroup.getGroupMembers().size() >= studyGroup.getMaxMembers()) {
            throw new CustomException(ErrorCode.STUDY_GROUP_FULL);
        }

        if (studyGroup.getGroupMembers().stream().anyMatch(groupMembers -> groupMembers.getUser().getUuid().equals(currentUser.getUuid()))) {
            throw new CustomException(ErrorCode.STUDY_GROUP_ALREADY_JOINED);
        }

        GroupMembers groupMembers = new GroupMembers();
        groupMembers.setUser(currentUser);
        groupMembers.setStudyGroup(studyGroup);
        groupMembers.setAdmin(false);
        groupMembersRepository.save(groupMembers);

        return ResponseEntity.ok().body(new MessageDto("스터디 그룹에 가입했습니다."));
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
