package com.credential.cubrism.server.qualification.controller;

import com.credential.cubrism.server.common.dto.ErrorDTO;
import com.credential.cubrism.server.qualification.service.QualificationListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QualificationController {
    private final QualificationListService qualificationApiService;

    @GetMapping("/qualification")
    public ResponseEntity<?> getQualification(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String code
    ) {
        try {
            if (type == null || type.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("'type' 파라미터가 필요합니다."));
            }

            return switch (type) {
                case "list" -> {
                    if (field == null || field.isEmpty()) {
                        yield ResponseEntity.ok(qualificationApiService.majorFieldListApi());
                    } else {
                        yield ResponseEntity.ok(qualificationApiService.qualificationListApi(field));
                    }
                }
                case "details" -> {
                    if (code == null || code.isEmpty()) {
                        yield ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("'code' 파라미터가 필요합니다."));
                    }
                    yield ResponseEntity.ok(qualificationApiService.qualificationDetailsApi(code));
                }
                default -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("'type' 파라미터가 잘못되었습니다."));
            };
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO(e.getMessage()));
        }
    }
}
