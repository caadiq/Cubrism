package com.credential.cubrism.server.qualification.service;

import com.credential.cubrism.server.qualification.dto.MajorFieldListApiDTO;
import com.credential.cubrism.server.qualification.dto.QualificationDetailsApiDTO;
import com.credential.cubrism.server.qualification.dto.QualificationListApiDTO;
import com.credential.cubrism.server.qualification.repository.QualificationDetailsRepository;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QualificationApiService {
    private final QualificationListRepository qualificationListRepository;
    private final QualificationDetailsRepository qualificationDetailsRepository;

    @Value("${cloud.aws.s3.bucket.qualificationIcon.url}")
    private String qualificationIconUrl;

    @Autowired
    public QualificationApiService(QualificationListRepository qualificationListRepository, QualificationDetailsRepository qualificationDetailsRepository) {
        this.qualificationListRepository = qualificationListRepository;
        this.qualificationDetailsRepository = qualificationDetailsRepository;
    }

    public List<MajorFieldListApiDTO> majorFieldListApi() {
        return qualificationListRepository.findDistinctMajorFieldNames().stream()
                .sorted()
                .map(majorFieldName -> {
                    String imageUrl = qualificationIconUrl + majorFieldName.replace(".", "_") + ".webp";
                    return new MajorFieldListApiDTO(majorFieldName, imageUrl);
                })
                .collect(Collectors.toList());
    }

    public List<QualificationListApiDTO> qualificationListApi(String field) {
        return qualificationListRepository.findByMajorFieldName(field).stream()
                .map(qualificationList -> new QualificationListApiDTO(
                        qualificationList.getMiddleFieldName(),
                        qualificationList.getCode(),
                        qualificationList.getName()
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
                .orElseThrow(() -> new RuntimeException("'" + code + "' code에 해당하는 자격증이 없습니다."));
    }
}
