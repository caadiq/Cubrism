package com.credential.cubrism.server.studygroup.controller;

import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.studygroup.dto.StudyGroupJoinRequestDto;
import com.credential.cubrism.server.studygroup.dto.StudyGroupCreateDto;
import com.credential.cubrism.server.studygroup.dto.StudyGroupGoalCreateDto;
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
@RequestMapping("/studygroup")
public class StudyGroupController {
    private final StudyGroupService studyGroupService;

    @PostMapping("/create") // 스터디 그룹 생성
    public ResponseEntity<MessageDto> createStudyGroup(@RequestBody StudyGroupCreateDto dto) {
        return studyGroupService.createStudyGroup(dto);
    }

    @GetMapping("/join") // 스터디 그룹 가입
    public ResponseEntity<MessageDto> joinStudyGroup(@RequestParam Long studyGroupId) {
        return studyGroupService.joinStudyGroup(studyGroupId);
    }

    @GetMapping("/approve") // 스터디 그룹 가입 승인
    public ResponseEntity<MessageDto> approveJoinRequest(@RequestParam UUID memberId) {
        return studyGroupService.approveJoinRequest(memberId);
    }

    @GetMapping("/join-requests") // 가입 요청 목록
    public List<StudyGroupJoinRequestDto> getAllJoinRequests() {
        return studyGroupService.getAllJoinRequests();
    }


    @GetMapping("/leave") // 스터디 그룹 탈퇴
    public ResponseEntity<MessageDto> leaveStudyGroup(@RequestParam Long studyGroupId) {
        return studyGroupService.leaveStudyGroup(studyGroupId);
    }

    @GetMapping("/delete") // 스터디 그룹 삭제
    public ResponseEntity<MessageDto> deleteStudyGroup(@RequestParam Long studyGroupId) {
        return studyGroupService.deleteStudyGroup(studyGroupId);
    }

    @GetMapping("/list") // 스터디 그룹 목록
    public ResponseEntity<StudyGroupListDto> studyGroupList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "false") boolean recruiting
    ) {
        limit = Math.max(1, Math.min(limit, 50)); // 한 페이지의 스터디 그룹 수를 1~50 사이로 제한
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending()); // 페이징 처리 (날짜순으로 정렬)

        return studyGroupService.studyGroupList(pageable, recruiting);
    }

    @GetMapping("/my") // 내 스터디 그룹 목록
    public ResponseEntity<StudyGroupListDto> myStudyGroupList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "false") boolean recruiting
    ) {
        limit = Math.max(1, Math.min(limit, 50)); // 한 페이지의 스터디 그룹 수를 1~50 사이로 제한
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending()); // 페이징 처리 (날짜순으로 정렬)

        return studyGroupService.myStudyGroupList(pageable, recruiting);
    }

    @GetMapping("/info") // 스터디 그룹 정보
    public ResponseEntity<?> studyGroupInfo(@RequestParam Long studyGroupId) {
        return studyGroupService.studyGroupInfo(studyGroupId);
    }

    @PostMapping("/addStudyGroupGoal") // 스터디 그룹 목표 추가
    public ResponseEntity<MessageDto> addStudyGroupGoal(@RequestBody StudyGroupGoalCreateDto dto) {
        return studyGroupService.addStudyGroupGoal(dto);
    }

    @GetMapping("/deleteStudyGroupGoal") // 스터디 그룹 목표 삭제
    public ResponseEntity<?> deleteStudyGroupGoal(@RequestParam Long goalId) {
        return studyGroupService.deleteGoalFromStudyGroup(goalId);
    }
}
