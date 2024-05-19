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
    public ResponseEntity<List<StudyGroupListDto.StudyGroupList>> myStudyGroupList() {
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
    public ResponseEntity<MessageDto> deleteStudyGroupGoal(@PathVariable Long goalId) {
        return studyGroupService.deleteStudyGroupGoal(goalId);
    }

    @PutMapping("/studygroup/goal/{goalId}") // 스터디 그룹 목표 완료
    public ResponseEntity<MessageDto> completeStudyGroupGoal(@PathVariable Long goalId) {
        return studyGroupService.completeStudyGroupGoal(goalId);
    }

    @PostMapping("/studygroup/goal/submit") // 스터디 그룹 목표 제출
    public ResponseEntity<MessageDto> submitStudyGroupGoal(@RequestBody StudyGroupGoalSubmitDto dto) {
        return studyGroupService.submitStudyGroupGoal(dto);
    }

    @GetMapping("/studygroup/goal/submits/{groupId}") // 스터디 그룹 목표 제출 목록
    public ResponseEntity<List<StudyGroupGoalSubmitListDto>> getStudyGroupGoalSubmits(@PathVariable Long groupId) {
        return studyGroupService.studyGroupGoalSubmitList(groupId);
    }

    // 스터디 그룹의 목표 리스트
    @GetMapping("/studygroup/{groupId}/goals")
    public ResponseEntity<List<StudyGroupGoalDto>> getStudyGroupGoals(@PathVariable Long groupId) {
        return studyGroupService.getStudyGroupGoals(groupId);
    }

    // 스터디 그룹 D-day 설정
    @PostMapping("/studygroup/dday")
    public ResponseEntity<MessageDto> setStudyGroupDDay(@RequestBody StudyGroupDDayDto dto) {
        return studyGroupService.setStudyGroupDDay(dto);
    }

    // 스터디 그룹 D-day 조회
    @GetMapping("/studygroup/{groupId}/dday")
    public ResponseEntity<StudyGroupDDayDto> getStudyGroupDDay(@PathVariable Long groupId) {
        return studyGroupService.getStudyGroupDDay(groupId);
    }

    // 스터디 그룹 들어갔을 때 필요한 정보
    @GetMapping("/studygroup/{groupId}/enter")
    public ResponseEntity<StudyGroupEnterDto> getStudyGroupEnterInfo(@PathVariable Long groupId) {
        return studyGroupService.getStudyGroupEnterInfo(groupId);
    }
}
