package com.credential.cubrism.server.qualification.controller;

import com.credential.cubrism.server.qualification.service.QualificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qualification")
public class QualificationController {
    private final QualificationService qualificationApiService;

    @GetMapping("/list/majorfield") // 대직무분야명 목록
    public ResponseEntity<?> majorFieldList() {
        return qualificationApiService.majorFieldList();
    }

    @GetMapping("/list/middlefield") // 중직무분야명 목록
    public ResponseEntity<?> middleFieldList(@RequestParam String field) {
        return qualificationApiService.middleFieldList(field);
    }

    @GetMapping("/details") // 자격증 세부정보
    public ResponseEntity<?> qualificationDetails(@RequestParam String code) {
        return qualificationApiService.qualificationDetails(code);
    }
}
