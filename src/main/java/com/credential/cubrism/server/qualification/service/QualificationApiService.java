package com.credential.cubrism.server.qualification.service;

import com.credential.cubrism.server.qualification.dto.QualificationDetailsApiDTO;
import com.credential.cubrism.server.qualification.dto.QualificationListApiDTO;
import com.credential.cubrism.server.qualification.repository.QualificationDetailsRepository;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QualificationApiService {
    private final QualificationListRepository qualificationListRepository;
    private final QualificationDetailsRepository qualificationDetailsRepository;

    @Autowired
    public QualificationApiService(QualificationListRepository qualificationListRepository, QualificationDetailsRepository qualificationDetailsRepository) {
        this.qualificationListRepository = qualificationListRepository;
        this.qualificationDetailsRepository = qualificationDetailsRepository;
    }

    public List<QualificationListApiDTO> qualificationListApi() {
        return qualificationListRepository.findAll().stream()
                .map(qualificationList -> new QualificationListApiDTO(
                        qualificationList.getCode(),
                        qualificationList.getName(),
                        qualificationList.getMiddleFieldName(),
                        qualificationList.getMajorFieldName()
                ))
                .collect(Collectors.toList());
    }

    public QualificationDetailsApiDTO qualificationDetailsApi(String code) {
        return qualificationDetailsRepository.findById(code)
                .map(qualificationDetails -> new QualificationDetailsApiDTO(
                        qualificationDetails.getCode(),
                        qualificationDetails.getExamSchedules().stream()
                                .map(schedule -> new QualificationDetailsApiDTO.Schedule(
                                        schedule.getCategory(),
                                        schedule.getWrittenApp(),
                                        schedule.getWrittenExam(),
                                        schedule.getWrittenExamResult(),
                                        schedule.getPracticalApp(),
                                        schedule.getPracticalExam(),
                                        schedule.getPracticalExamResult()
                                ))
                                .collect(Collectors.toList()),
                        new QualificationDetailsApiDTO.Fee(
                                qualificationDetails.getExamFees().getWrittenFee(),
                                qualificationDetails.getExamFees().getPracticalFee()
                        ),
                        qualificationDetails.getTendency(),
                        qualificationDetails.getExamStandards().stream()
                                .map(standard -> new QualificationDetailsApiDTO.Standard(
                                        standard.getFilePath(),
                                        standard.getFileName()
                                ))
                                .collect(Collectors.toList()),
                        qualificationDetails.getPublicQuestions().stream()
                                .map(question -> new QualificationDetailsApiDTO.Question(
                                        question.getFilePath(),
                                        question.getFileName()
                                ))
                                .collect(Collectors.toList()),
                        qualificationDetails.getAcquisition(),
                        qualificationDetails.getRecommendBooks().stream()
                                .map(book -> new QualificationDetailsApiDTO.Books(
                                        book.getTitle(),
                                        book.getAuthors(),
                                        book.getPublisher(),
                                        book.getDate(),
                                        book.getPrice(),
                                        book.getSalePrice(),
                                        book.getThumbnail(),
                                        book.getUrl()
                                ))
                                .collect(Collectors.toList())
                ))
                .orElseThrow(() -> new RuntimeException(code + " code에 해당하는 자격증이 없습니다."));
    }
}
