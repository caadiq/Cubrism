package com.credential.cubrism.server.qualification.service;

import com.credential.cubrism.server.qualification.dto.QualificationDetailsRequestDTO;
import com.credential.cubrism.server.qualification.dto.QualificationDetailsResponseDTO;
import com.credential.cubrism.server.qualification.model.*;
import com.credential.cubrism.server.qualification.repository.QualificationDetailsRepository;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QualificationDetailsService {
    private final QualificationListRepository qualificationListRepository;
    private final QualificationDetailsRepository qualificationDetailsRepository;
    private final WebClient webClient;

    @Autowired
    public QualificationDetailsService(QualificationListRepository qualificationListRepository, QualificationDetailsRepository qualificationDetailsRepository, WebClient webClient) {
        this.qualificationListRepository = qualificationListRepository;
        this.qualificationDetailsRepository = qualificationDetailsRepository;
        this.webClient = webClient;
    }

    public void getQualificationDetails() {
        String url = "http://localhost:8000/qualification";

        List<QualificationList> qualificationList = qualificationListRepository.findAll();

        List<QualificationDetailsRequestDTO> qualificationDetailsRequestDTO = qualificationList.stream()
                .map(qualification -> new QualificationDetailsRequestDTO(qualification.getCode(), qualification.getName()))
                .collect(Collectors.toList());

        webClient.post()
                .uri(url)
                .bodyValue(qualificationDetailsRequestDTO)
                .retrieve()
                .bodyToFlux(QualificationDetailsResponseDTO.class)
                .subscribe(this::saveQualificationDetails);
    }

    public void saveQualificationDetails(QualificationDetailsResponseDTO response) {
        // 데이터가 중복되지 않도록 기존 데이터 삭제
        qualificationDetailsRepository.deleteByCode(response.getCode());

        QualificationDetails qualificationDetails = new QualificationDetails();
        qualificationDetails.setCode(response.getCode());
        qualificationDetails.setTendency(response.getTendency());
        qualificationDetails.setAcquisition(response.getAcquisition());

        List<ExamSchedules> examSchedulesList = Optional.ofNullable(response.getSchedule()).orElse(Collections.emptyList()).stream()
                .map(schedule -> {
                    ExamSchedules examSchedules = new ExamSchedules();
                    examSchedules.setScheduleId(UUID.randomUUID());
                    examSchedules.setCode(response.getCode());
                    examSchedules.setCategory(schedule.getCategory()); // 구분
                    examSchedules.setWrittenApp(schedule.getWrittenApp()); // 필기 원서 접수
                    examSchedules.setWrittenExam(schedule.getWrittenExam()); // 필기 시험
                    examSchedules.setWrittenExamResult(schedule.getWrittenExamResult()); // 필기 합격 발표
                    examSchedules.setPracticalApp(schedule.getPracticalApp()); // 실기 원서 접수
                    examSchedules.setPracticalExam(schedule.getPracticalExam()); // 실기 시험
                    examSchedules.setPracticalExamResult(schedule.getPracticalExamResult()); // 최종 합격자 발표일
                    return examSchedules;
                }).collect(Collectors.toList());
        qualificationDetails.setExamSchedules(examSchedulesList);

        ExamFees examFees = new ExamFees();
        examFees.setCode(response.getCode());
        examFees.setWrittenFee(Optional.ofNullable(response.getFee()).map(QualificationDetailsResponseDTO.Fee::getWrittenFee).orElse(null)); // 필기 수수료
        examFees.setPracticalFee(Optional.ofNullable(response.getFee()).map(QualificationDetailsResponseDTO.Fee::getPracticalFee).orElse(null)); // 실기 수수료
        qualificationDetails.setExamFees(examFees);

        List<ExamStandards> examStandardsList = Optional.ofNullable(response.getStandard()).orElse(Collections.emptyList()).stream()
                .map(standard -> {
                    ExamStandards examStandards = new ExamStandards();
                    examStandards.setFileId(UUID.randomUUID());
                    examStandards.setCode(response.getCode());
                    examStandards.setFilePath(standard.getFilePath()); // 파일 경로
                    examStandards.setFileName(standard.getFileName()); // 파일 이름
                    return examStandards;
                }).collect(Collectors.toList());
        qualificationDetails.setExamStandards(examStandardsList);

        List<PublicQuestions> publicQuestionsList = Optional.ofNullable(response.getQuestion()).orElse(Collections.emptyList()).stream()
                .map(question -> {
                    PublicQuestions publicQuestions = new PublicQuestions();
                    publicQuestions.setFileId(UUID.randomUUID());
                    publicQuestions.setCode(response.getCode());
                    publicQuestions.setFilePath(question.getFilePath()); // 파일 경로
                    publicQuestions.setFileName(question.getFileName()); // 파일 이름
                    return publicQuestions;
                }).collect(Collectors.toList());
        qualificationDetails.setPublicQuestions(publicQuestionsList);

        // 자격증 상세정보 저장
        qualificationDetailsRepository.save(qualificationDetails);
    }
}
