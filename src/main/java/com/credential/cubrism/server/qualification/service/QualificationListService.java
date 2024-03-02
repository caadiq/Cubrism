package com.credential.cubrism.server.qualification.service;

import com.credential.cubrism.server.qualification.dto.MajorFieldGetDTO;
import com.credential.cubrism.server.qualification.dto.QualificationDetailsGetDTO;
import com.credential.cubrism.server.qualification.dto.MiddleFieldGetDTO;
import com.credential.cubrism.server.qualification.repository.QualificationDetailsRepository;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QualificationListService {
    private final QualificationListRepository qualificationListRepository;
    private final QualificationDetailsRepository qualificationDetailsRepository;

    @Value("${cloud.aws.s3.bucket.qualificationIcon.url}")
    private String qualificationIconUrl;

    @Autowired
    public QualificationListService(QualificationListRepository qualificationListRepository, QualificationDetailsRepository qualificationDetailsRepository) {
        this.qualificationListRepository = qualificationListRepository;
        this.qualificationDetailsRepository = qualificationDetailsRepository;
    }

    public List<MajorFieldGetDTO> majorFieldListApi() {
        return qualificationListRepository.findDistinctMajorFieldNames().stream()
                .sorted()
                .map(majorFieldName -> {
                    String imageUrl = qualificationIconUrl + majorFieldName.replace(".", "_") + ".webp";
                    return new MajorFieldGetDTO(majorFieldName, imageUrl);
                })
                .collect(Collectors.toList());
    }

    public List<MiddleFieldGetDTO> qualificationListApi(String field) {
        return qualificationListRepository.findByMajorFieldName(field).stream()
                .map(qualificationList -> new MiddleFieldGetDTO(
                        qualificationList.getMiddleFieldName(),
                        qualificationList.getCode(),
                        qualificationList.getName()
                ))
                .collect(Collectors.toList());
    }

    public QualificationDetailsGetDTO qualificationDetailsApi(String code) {
        return qualificationDetailsRepository.findById(code)
                .map(qualificationDetails -> new QualificationDetailsGetDTO(
                        qualificationDetails.getCode(),
                        qualificationDetails.getExamSchedules().stream()
                                .map(schedule -> new QualificationDetailsGetDTO.Schedule(
                                        schedule.getCategory(),
                                        schedule.getWrittenApp(),
                                        schedule.getWrittenExam(),
                                        schedule.getWrittenExamResult(),
                                        schedule.getPracticalApp(),
                                        schedule.getPracticalExam(),
                                        schedule.getPracticalExamResult()
                                ))
                                .collect(Collectors.toList()),
                        new QualificationDetailsGetDTO.Fee(
                                qualificationDetails.getExamFees().getWrittenFee(),
                                qualificationDetails.getExamFees().getPracticalFee()
                        ),
                        qualificationDetails.getTendency(),
                        qualificationDetails.getExamStandards().stream()
                                .map(standard -> new QualificationDetailsGetDTO.Standard(
                                        standard.getFilePath(),
                                        standard.getFileName()
                                ))
                                .collect(Collectors.toList()),
                        qualificationDetails.getPublicQuestions().stream()
                                .map(question -> new QualificationDetailsGetDTO.Question(
                                        question.getFilePath(),
                                        question.getFileName()
                                ))
                                .collect(Collectors.toList()),
                        qualificationDetails.getAcquisition(),
                        qualificationDetails.getRecommendBooks().stream()
                                .map(book -> new QualificationDetailsGetDTO.Books(
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
                .orElseThrow(() -> new RuntimeException("'" + code + "' code에 해당하는 자격증이 없습니다."));
    }
}
