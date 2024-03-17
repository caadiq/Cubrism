package com.credential.cubrism.server.studygroup.service;

import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.authentication.utils.AuthenticationUtil;
import com.credential.cubrism.server.studygroup.dto.StudyGroupCreatePostDTO;
import com.credential.cubrism.server.studygroup.dto.StudyGroupListGetDTO;
import com.credential.cubrism.server.studygroup.entity.GroupMembers;
import com.credential.cubrism.server.studygroup.entity.GroupTags;
import com.credential.cubrism.server.studygroup.entity.StudyGroup;
import com.credential.cubrism.server.studygroup.repository.GroupMembersRepository;
import com.credential.cubrism.server.studygroup.repository.GroupTagsRepository;
import com.credential.cubrism.server.studygroup.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMembersRepository groupMembersRepository;
    private final GroupTagsRepository groupTagsRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createStudyGroup(StudyGroupCreatePostDTO dto, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);

        StudyGroup studyGroup = new StudyGroup();
        studyGroup.setGroupName(dto.getGroupName());
        studyGroup.setGroupDescription(dto.getGroupDescription());
        studyGroup.setMaxMembers(dto.getMaxMembers());

        List<GroupTags> groupTagsList = new ArrayList<>();
        for (StudyGroupCreatePostDTO.Tags tag : dto.getTags()) {
            GroupTags groupTags = new GroupTags();
            groupTags.setStudyGroup(studyGroup);
            groupTags.setTagName(tag.getTagName());
            groupTagsList.add(groupTags);
        }
        studyGroup.setGroupTags(groupTagsList);

        GroupMembers groupMembers = new GroupMembers();
        groupMembers.setUser(user);
        groupMembers.setStudyGroup(studyGroup);
        groupMembers.setAdmin(true);
        studyGroup.setGroupMembers(List.of(groupMembers));

        studyGroupRepository.save(studyGroup);
    }

    public StudyGroupListGetDTO studyGroupList(Pageable pageable) {
        Page<StudyGroup> studyGroup = studyGroupRepository.findAll(pageable);

        StudyGroupListGetDTO.Pageable pageableDTO = new StudyGroupListGetDTO.Pageable(
                studyGroup.hasPrevious() ? pageable.getPageNumber() - 1 : null,
                pageable.getPageNumber(),
                studyGroup.hasNext() ? pageable.getPageNumber() + 1 : null
        );

        List<StudyGroupListGetDTO.StudyGroupList> studyGroupListDTO = studyGroup.stream()
                .map(group -> {
                    boolean isRecruiting = group.getGroupMembers().size() < group.getMaxMembers(); // 모집중 여부
                    return new StudyGroupListGetDTO.StudyGroupList(
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
                }).toList();

        return new StudyGroupListGetDTO(pageableDTO, studyGroupListDTO);
    }

    public void joinStudyGroup(Long groupId, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Study group not found"));

        if (studyGroup.getGroupMembers().size() >= studyGroup.getMaxMembers()) {
            throw new IllegalArgumentException("Study group is full");
        }

        if(studyGroup.getGroupMembers().stream().anyMatch(groupMembers -> groupMembers.getUser().getUuid().equals(user.getUuid()))) {
            throw new IllegalArgumentException("You are already a member of this study group");
        }

        GroupMembers groupMembers = new GroupMembers();
        groupMembers.setUser(user);
        groupMembers.setStudyGroup(studyGroup);
        groupMembers.setAdmin(false);
        groupMembersRepository.save(groupMembers);
    }

    public void leaveStudyGroup(Long groupId, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Study group not found"));

        GroupMembers groupMembers = groupMembersRepository.findByUserAndStudyGroup(user, studyGroup)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this study group"));

        if (groupMembers.isAdmin()) {
            throw new IllegalArgumentException("You are the admin of this study group");
        }

        groupMembersRepository.delete(groupMembers);
    }
}
