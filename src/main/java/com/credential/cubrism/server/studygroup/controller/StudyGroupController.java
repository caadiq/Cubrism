package com.credential.cubrism.server.studygroup.controller;

import com.credential.cubrism.server.studygroup.dto.JoinRequestDto;
import com.credential.cubrism.server.studygroup.dto.StudyGroupCreateDto;
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
    public ResponseEntity<?> createStudyGroup(@RequestBody StudyGroupCreateDto dto) {
        return studyGroupService.createStudyGroup(dto);
    }

    @GetMapping("/join") // 스터디 그룹 가입
    public ResponseEntity<?> joinStudyGroup(@RequestParam Long studyGroupId) {
        return studyGroupService.joinStudyGroup(studyGroupId);
    }

    @GetMapping("/approve") // 스터디 그룹 가입 승인
    public ResponseEntity<?> approveJoinRequest(@RequestParam UUID memberId) {
        return studyGroupService.approveJoinRequest(memberId);
    }

    @GetMapping("/join-requests") // 가입 요청 목록
    public List<JoinRequestDto> getAllJoinRequests() {
        return studyGroupService.getAllJoinRequests();
    }


    @GetMapping("/leave") // 스터디 그룹 탈퇴
    public ResponseEntity<?> leaveStudyGroup(@RequestParam Long studyGroupId) {
        return studyGroupService.leaveStudyGroup(studyGroupId);
    }

    @GetMapping("/delete") // 스터디 그룹 삭제
    public ResponseEntity<?> deleteStudyGroup(@RequestParam Long studyGroupId) {
        return studyGroupService.deleteStudyGroup(studyGroupId);
    }

    @GetMapping("/list") // 스터디 그룹 목록
    public ResponseEntity<?> studyGroupList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        limit = Math.max(1, Math.min(limit, 50)); // 한 페이지의 스터디 그룹 수를 1~50 사이로 제한
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending()); // 페이징 처리 (날짜순으로 정렬)

        return studyGroupService.studyGroupList(pageable);
    }

    @GetMapping("/my") // 내 스터디 그룹 목록
    public ResponseEntity<?> myStudyGroupList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        limit = Math.max(1, Math.min(limit, 50)); // 한 페이지의 스터디 그룹 수를 1~50 사이로 제한
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending()); // 페이징 처리 (날짜순으로 정렬)

        return studyGroupService.myStudyGroupList(pageable);
    }
}
