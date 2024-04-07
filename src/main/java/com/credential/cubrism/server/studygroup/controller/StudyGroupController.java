package com.credential.cubrism.server.studygroup.controller;

import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.studygroup.dto.StudyGroupAddGoalDto;
import com.credential.cubrism.server.studygroup.dto.StudyGroupCreateDto;
import com.credential.cubrism.server.studygroup.dto.StudyGroupJoinListDto;
import com.credential.cubrism.server.studygroup.dto.StudyGroupListDto;
import com.credential.cubrism.server.studygroup.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StudyGroupController {
    private final StudyGroupService studyGroupService;

    @PostMapping("/studygroup") // 스터디 그룹 생성
    public ResponseEntity<MessageDto> createStudyGroup(@RequestBody StudyGroupCreateDto dto) {
        return studyGroupService.createStudyGroup(dto);
    }

    @DeleteMapping("/studygroup/{groupId}") // 스터디 그룹 삭제
    public ResponseEntity<MessageDto> deleteStudyGroup(@PathVariable Long groupId) {
        return studyGroupService.deleteStudyGroup(groupId);
    }

//    @PutMapping("/studygroup/{groupId}") // 스터디 그룹 수정
//    public ResponseEntity<MessageDto> updateStudyGroup(@PathVariable Long groupId), @RequestBody StudyGroupUpdateDto dto) {
//        return studyGroupService.updateStudyGroup(studyGroupId, dto);
//    }

    @GetMapping("/studygroup/{groupId}") // 스터디 그룹 정보
    public ResponseEntity<?> studyGroupInfo(@PathVariable Long groupId) {
        return studyGroupService.studyGroupInfo(groupId);
    }

    @PostMapping("/studygroup/join/{groupId}") // 스터디 그룹 가입 요청
    public ResponseEntity<MessageDto> requestJoin(@PathVariable Long groupId) {
        return studyGroupService.requestJoin(groupId);
    }

    @GetMapping("/studygroup/join") // 가입 요청 목록
    public ResponseEntity<List<StudyGroupJoinListDto>> getJoinRequest() {
        return studyGroupService.getJoinRequest();
    }

    @PutMapping("/studygroup/join/{memberId}") // 스터디 그룹 가입 승인
    public ResponseEntity<MessageDto> approveJoinRequest(@PathVariable UUID memberId) {
        return studyGroupService.approveJoinRequest(memberId);
    }

//    @DeleteMapping("/studygroup/join/{memberId}") // 스터디 그룹 가입 거절
//    public ResponseEntity<MessageDto> denyJoinRequest(@PathVariable UUID memberId) {
//        return studyGroupService.denyJoinRequest(memberId);
//    }

    @DeleteMapping("/studygroup/leave/{groupId}") // 스터디 그룹 탈퇴
    public ResponseEntity<MessageDto> leaveStudyGroup(@PathVariable Long groupId) {
        return studyGroupService.leaveStudyGroup(groupId);
    }

    @GetMapping("/studygroups") // 스터디 그룹 목록
    public ResponseEntity<StudyGroupListDto> studyGroupList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "false") boolean recruiting
    ) {
        limit = Math.max(1, Math.min(limit, 50)); // 한 페이지의 스터디 그룹 수를 1~50 사이로 제한
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending()); // 페이징 처리 (날짜순으로 정렬)

        return studyGroupService.studyGroupList(pageable, recruiting);
    }

    @GetMapping("/studygroups/my") // 내 스터디 그룹 목록
    public ResponseEntity<StudyGroupListDto> myStudyGroupList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "false") boolean recruiting
    ) {
        limit = Math.max(1, Math.min(limit, 50)); // 한 페이지의 스터디 그룹 수를 1~50 사이로 제한
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending()); // 페이징 처리 (날짜순으로 정렬)

        return studyGroupService.myStudyGroupList(pageable, recruiting);
    }

    @PostMapping("/studygroup/goal") // 스터디 그룹 목표 추가
    public ResponseEntity<MessageDto> addStudyGroupGoal(@RequestBody StudyGroupAddGoalDto dto) {
        return studyGroupService.addStudyGroupGoal(dto);
    }

    @DeleteMapping("/studygroup/goal/{goalId}") // 스터디 그룹 목표 삭제
    public ResponseEntity<?> deleteStudyGroupGoal(@PathVariable Long goalId) {
        return studyGroupService.deleteStudyGroupGoal(goalId);
    }

//    @PutMapping("/studygroup/goal/{goalId}") // 스터디 그룹 목표 수정
//    public ResponseEntity<?> updateStudyGroupGoal(@PathVariable Long goalId, @RequestBody StudyGroupUpdateGoalDto dto) {
//        return studyGroupService.updateStudyGroupGoal(goalId, dto);
//    }

//    @GetMapping("/studygroup/goal/{goalId}") // 스터디 그룹 목표 정보
//    public ResponseEntity<?> studyGroupGoal(@PathVariable Long goalId) {
//        return studyGroupService.studyGroupGoal(goalId);
//    }
}
