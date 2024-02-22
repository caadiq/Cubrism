package com.credential.cubrism.server.qualification.controller;

import com.credential.cubrism.server.qualification.service.QualificationListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cubrism")
public class QualificationListController {
    private final QualificationListService qualificationListService;

    @Autowired
    public QualificationListController(QualificationListService qualificationListService) {
        this.qualificationListService = qualificationListService;
    }

    @GetMapping("/qualificationlist")
    public ResponseEntity<?> getQualificationList() {
        try {
            return ResponseEntity.ok(qualificationListService.returnQualificationList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
