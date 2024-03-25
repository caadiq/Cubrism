package com.credential.cubrism.server.qualification.service;

import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.qualification.dto.QualificationListDto;
import com.credential.cubrism.server.qualification.dto.MajorFieldDto;
import com.credential.cubrism.server.qualification.dto.MiddleFieldDto;
import com.credential.cubrism.server.qualification.dto.QualificationDetailsDto;
import com.credential.cubrism.server.qualification.entity.ExamSchedules;
import com.credential.cubrism.server.qualification.repository.QualificationDetailsRepository;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QualificationService {
    @Value("${cloud.aws.s3.bucket.qualificationIcon.url}")
    private String qualificationIconUrl;

    private final QualificationListRepository qualificationListRepository;
    private final QualificationDetailsRepository qualificationDetailsRepository;

    @Cacheable("qualificationlist")
    public List<QualificationListDto> qualificationList() {
        return qualificationListRepository.findAll().stream()
                .map(qualificationList -> new QualificationListDto(
                        qualificationList.getCode(),
                        qualificationList.getName()
                ))
                .sorted(Comparator.comparing(QualificationListDto::getName))
                .toList();
    }

    // 대직무분야명 목록
    public ResponseEntity<List<MajorFieldDto>> majorFieldList() {
        List<MajorFieldDto> majorFieldList = qualificationListRepository.findDistinctMajorFieldNames().stream()
                .sorted()
                .map(majorFieldName -> {
                    String imageUrl = qualificationIconUrl + majorFieldName.replace(".", "_") + ".webp";
                    return new MajorFieldDto(majorFieldName, imageUrl);
                }).toList();

        return ResponseEntity.status(HttpStatus.OK).body(majorFieldList);
    }

    // 중직무분야명 목록
    public ResponseEntity<List<MiddleFieldDto>> middleFieldList(String field) {
        List<MiddleFieldDto> middleFieldList = qualificationListRepository.findByMajorFieldName(field).stream()
                .map(qualificationList -> new MiddleFieldDto(
                        qualificationList.getMiddleFieldName(),
                        qualificationList.getCode(),
                        qualificationList.getName()
                )).toList();

        return ResponseEntity.status(HttpStatus.OK).body(middleFieldList);
    }

    // 자격증 세부정보
    public ResponseEntity<QualificationDetailsDto> qualificationDetails(String code) {
        QualificationDetailsDto dto = qualificationDetailsRepository.findById(code)
                .map(qualificationDetails -> new QualificationDetailsDto(
                        qualificationDetails.getCode(),
                        qualificationDetails.getQualificationList().getName(),
                        qualificationDetails.getExamSchedules().stream()
                                .sorted(Comparator.comparing(ExamSchedules::getCategory))
                                .map(schedule -> new QualificationDetailsDto.Schedule(
                                        schedule.getCategory(),
                                        schedule.getWrittenApp(),
                                        schedule.getWrittenExam(),
                                        schedule.getWrittenExamResult(),
                                        schedule.getPracticalApp(),
                                        schedule.getPracticalExam(),
                                        schedule.getPracticalExamResult()
                                )).toList(),
                        new QualificationDetailsDto.Fee(
                                qualificationDetails.getExamFees().getWrittenFee(),
                                qualificationDetails.getExamFees().getPracticalFee()
                        ),
                        qualificationDetails.getTendency(),
                        qualificationDetails.getExamStandards().stream()
                                .map(standard -> new QualificationDetailsDto.Standard(
                                        standard.getFilePath(),
                                        standard.getFileName()
                                )).toList(),
                        qualificationDetails.getPublicQuestions().stream()
                                .map(question -> new QualificationDetailsDto.Question(
                                        question.getFilePath(),
                                        question.getFileName()
                                )).toList(),
                        qualificationDetails.getAcquisition(),
                        qualificationDetails.getRecommendBooks().stream()
                                .map(book -> new QualificationDetailsDto.Books(
                                        book.getTitle(),
                                        book.getAuthors(),
                                        book.getPublisher(),
                                        book.getDate(),
                                        book.getPrice(),
                                        book.getSalePrice(),
                                        book.getThumbnail(),
                                        book.getUrl()
                                )).toList()
                ))
                .orElseThrow(() -> new CustomException(ErrorCode.QUALIFICATION_NOT_FOUND));

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
}
