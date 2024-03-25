package com.credential.cubrism.server.qualification.controller;

import com.credential.cubrism.server.qualification.dto.MajorFieldDto;
import com.credential.cubrism.server.qualification.dto.MiddleFieldDto;
import com.credential.cubrism.server.qualification.dto.QualificationDetailsDto;
import com.credential.cubrism.server.qualification.dto.QualificationListDto;
import com.credential.cubrism.server.qualification.service.QualificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qualification")
public class QualificationController {
    private final QualificationService qualificationApiService;

    @GetMapping("/list/all") // 자격증 목록
    public ResponseEntity<List<QualificationListDto>> qualificationList() {
        List<QualificationListDto> categoryList = qualificationApiService.qualificationList();
        return ResponseEntity.status(HttpStatus.OK).body(categoryList);
    }

    @GetMapping("/list/majorfield") // 대직무분야명 목록
    public ResponseEntity<List<MajorFieldDto>> majorFieldList() {
        return qualificationApiService.majorFieldList();
    }

    @GetMapping("/list/middlefield") // 중직무분야명 목록
    public ResponseEntity<List<MiddleFieldDto>> middleFieldList(@RequestParam String field) {
        return qualificationApiService.middleFieldList(field);
    }

    @GetMapping("/details") // 자격증 세부정보
    public ResponseEntity<QualificationDetailsDto> qualificationDetails(@RequestParam String code) {
        return qualificationApiService.qualificationDetails(code);
    }
}
