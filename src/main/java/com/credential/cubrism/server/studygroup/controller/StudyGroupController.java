package com.credential.cubrism.server.studygroup.controller;

import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.studygroup.dto.*;
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

    @GetMapping("/studygroups/my") // 내가 가입한 스터디 그룹 목록
    public ResponseEntity<StudyGroupListDto.StudyGroupList> myStudyGroupList() {
        return studyGroupService.myStudyGroupList();
    }

    @GetMapping("/studygroup/{groupId}") // 스터디 그룹 정보
    public ResponseEntity<StudyGroupInfoDto> studyGroupInfo(@PathVariable Long groupId) {
        return studyGroupService.studyGroupInfo(groupId);
    }



    @PostMapping("/studygroup/join/request/{groupId}") // 스터디 그룹 가입 신청
    public ResponseEntity<MessageDto> requestJoin(@PathVariable Long groupId) {
        return studyGroupService.requestJoin(groupId);
    }

    @DeleteMapping("/studygroup/join/request/{memberId}") // 스터디 그룹 가입 신청 취소
    public ResponseEntity<MessageDto> cancelJoinRequest(@PathVariable UUID memberId) {
        return studyGroupService.cancelJoinRequest(memberId);
    }

    @GetMapping("/studygroup/join/requests") // 가입 신청한 스터디 그룹 목록
    public ResponseEntity<List<StudyGroupJoinListDto>> joinRequestList() {
        return studyGroupService.joinRequestList();
    }



    @PutMapping("/studygroup/join/receive/{memberId}") // 스터디 그룹 가입 승인
    public ResponseEntity<MessageDto> approveJoinRequest(@PathVariable UUID memberId) {
        return studyGroupService.approveJoinRequest(memberId);
    }

    @DeleteMapping("/studygroup/join/receive/{memberId}") // 스터디 그룹 가입 거절
    public ResponseEntity<MessageDto> denyJoinRequest(@PathVariable UUID memberId) {
        return studyGroupService.denyJoinRequest(memberId);
    }

    @GetMapping("/studygroup/join/receives/{groupId}") // 해당 스터디 그룹 가입 신청 목록
    public ResponseEntity<List<StudyGroupJoinRequestDto>> joinRequestList(@PathVariable Long groupId) {
        return studyGroupService.joinRequestList(groupId);
    }



    @PostMapping("/studygroup/goal") // 스터디 그룹 목표 추가
    public ResponseEntity<MessageDto> addStudyGroupGoal(@RequestBody StudyGroupAddGoalDto dto) {
        return studyGroupService.addStudyGroupGoal(dto);
    }

    @DeleteMapping("/studygroup/goal/{goalId}") // 스터디 그룹 목표 삭제
    public ResponseEntity<?> deleteStudyGroupGoal(@PathVariable Long goalId) {
        return studyGroupService.deleteStudyGroupGoal(goalId);
    }

    @PutMapping("/studygroup/goal/{goalId}") // 스터디 그룹 목표 수정
    public ResponseEntity<?> updateStudyGroupGoal(@PathVariable Long goalId, @RequestBody StudyGroupUpdateGoalDto dto) {
        return studyGroupService.updateStudyGroupGoal(goalId, dto);
    }

//    @GetMapping("/studygroup/goal/{goalId}") // 스터디 그룹 목표 정보
//    public ResponseEntity<?> studyGroupGoal(@PathVariable Long goalId) {
//        return studyGroupService.studyGroupGoal(goalId);
//    }

    // 스터디 그룹의 UserGoal 현황 조회
    @GetMapping("/studygroup/{groupId}/usergoals")
    public ResponseEntity<List<UserGoalStatusDto>> getUserGoals(@PathVariable Long groupId) {
        return studyGroupService.getUserGoals(groupId);
    }

    // 사용자의 세부사항 완료 처리
    @PostMapping("/studygroup/goal/complete")
    public ResponseEntity<MessageDto> completeStudyGoal(@RequestBody CompleteStudyGroupGoalDto dto) {
        return studyGroupService.completeStudyGroupGoal(dto.getGoalId());
    }
}
