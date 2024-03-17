package com.credential.cubrism.server.studygroup.controller;

import com.credential.cubrism.server.common.dto.ResultDTO;
import com.credential.cubrism.server.studygroup.dto.StudyGroupCreatePostDTO;
import com.credential.cubrism.server.studygroup.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studygroup")
public class StudyGroupController {
    private final StudyGroupService studyGroupService;

    @PostMapping("/create")
    public ResponseEntity<?> createStudyGroup(
            @RequestBody StudyGroupCreatePostDTO dto,
            Authentication authentication
    ) {
        try {
            studyGroupService.createStudyGroup(dto, authentication);
            return ResponseEntity.ok().body(new ResultDTO(true, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResultDTO(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResultDTO(false, e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> studyGroupList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        try {
            limit = Math.max(1, Math.min(limit, 50)); // 한 페이지의 스터디 그룹 수를 1~50 사이로 제한
            Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending()); // 페이징 처리 (날짜순으로 정렬)

            return ResponseEntity.ok().body(studyGroupService.studyGroupList(pageable));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResultDTO(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResultDTO(false, e.getMessage()));
        }
    }
}
