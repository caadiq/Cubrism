package com.credential.cubrism.server.qualification.service;

import com.credential.cubrism.server.qualification.dto.QualificationDetailsApiPostDTO;
import com.credential.cubrism.server.qualification.dto.QualificationDetailsApiResponseDTO;
import com.credential.cubrism.server.qualification.model.*;
import com.credential.cubrism.server.qualification.repository.QualificationDetailsRepository;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        List<QualificationDetailsApiPostDTO> qualificationDetailsApiPostDTO = qualificationList.stream()
                .map(qualification -> new QualificationDetailsApiPostDTO(qualification.getCode(), qualification.getName()))
                .collect(Collectors.toList());

        webClient.post()
                .uri(url)
                .bodyValue(qualificationDetailsApiPostDTO)
                .retrieve()
                .bodyToFlux(QualificationDetailsApiResponseDTO.class)
                .retryWhen(Retry.backoff(5, Duration.ofMinutes(1)).maxBackoff(Duration.ofSeconds(10)))
                .subscribe(this::saveQualificationDetails);
    }

    private void saveQualificationDetails(QualificationDetailsApiResponseDTO qualificationDetailsApiResponseDTO) {
        qualificationDetailsRepository.deleteByCode(qualificationDetailsApiResponseDTO.getCode()); // 데이터가 중복되지 않도록 기존 데이터 삭제
        QualificationDetails qualificationDetails = new QualificationDetails();
        qualificationDetails.setCode(qualificationDetailsApiResponseDTO.getCode());
        qualificationDetails.setTendency(qualificationDetailsApiResponseDTO.getTendency());
        qualificationDetails.setAcquisition(qualificationDetailsApiResponseDTO.getAcquisition());
        setExamSchedules(qualificationDetailsApiResponseDTO, qualificationDetails);
        setExamFees(qualificationDetailsApiResponseDTO, qualificationDetails);
        setExamStandards(qualificationDetailsApiResponseDTO, qualificationDetails);
        setPublicQuestions(qualificationDetailsApiResponseDTO, qualificationDetails);
        setRecommendBooks(qualificationDetailsApiResponseDTO, qualificationDetails);
        qualificationDetailsRepository.save(qualificationDetails);
    }

    private void setExamSchedules(QualificationDetailsApiResponseDTO qualificationDetailsApiResponseDTO, QualificationDetails qualificationDetails) {
        List<ExamSchedules> examSchedulesList = Optional.ofNullable(qualificationDetailsApiResponseDTO.getSchedule()).orElse(Collections.emptyList()).stream()
                .map(schedule -> {
                    ExamSchedules examSchedules = new ExamSchedules();
                    examSchedules.setCategory(schedule.getCategory()); // 구분
                    examSchedules.setWrittenApp(schedule.getWrittenApp()); // 필기 원서 접수
                    examSchedules.setWrittenExam(schedule.getWrittenExam()); // 필기 시험
                    examSchedules.setWrittenExamResult(schedule.getWrittenExamResult()); // 필기 합격 발표
                    examSchedules.setPracticalApp(schedule.getPracticalApp()); // 실기 원서 접수
                    examSchedules.setPracticalExam(schedule.getPracticalExam()); // 실기 시험
                    examSchedules.setPracticalExamResult(schedule.getPracticalExamResult()); // 최종 합격자 발표일
                    examSchedules.setQualificationDetails(qualificationDetails);
                    return examSchedules;
                }).collect(Collectors.toList());
        qualificationDetails.setExamSchedules(examSchedulesList);
    }

    private void setExamFees(QualificationDetailsApiResponseDTO dto, QualificationDetails qualificationDetails) {
        ExamFees examFees = new ExamFees();
        examFees.setCode(dto.getCode());
        examFees.setWrittenFee(Optional.ofNullable(dto.getFee()).map(QualificationDetailsApiResponseDTO.Fee::getWrittenFee).orElse(null)); // 필기 수수료
        examFees.setPracticalFee(Optional.ofNullable(dto.getFee()).map(QualificationDetailsApiResponseDTO.Fee::getPracticalFee).orElse(null)); // 실기 수수료
        qualificationDetails.setExamFees(examFees);
    }

    private void setExamStandards(QualificationDetailsApiResponseDTO dto, QualificationDetails qualificationDetails) {
        List<ExamStandards> examStandardsList = Optional.ofNullable(dto.getStandard()).orElse(Collections.emptyList()).stream()
                .map(standard -> {
                    ExamStandards examStandards = new ExamStandards();
                    examStandards.setFilePath(standard.getFilePath()); // 파일 경로
                    examStandards.setFileName(standard.getFileName()); // 파일 이름
                    examStandards.setQualificationDetails(qualificationDetails);
                    return examStandards;
                }).collect(Collectors.toList());
        qualificationDetails.setExamStandards(examStandardsList);
    }

    private void setPublicQuestions(QualificationDetailsApiResponseDTO dto, QualificationDetails qualificationDetails) {
        List<PublicQuestions> publicQuestionsList = Optional.ofNullable(dto.getQuestion()).orElse(Collections.emptyList()).stream()
                .map(question -> {
                    PublicQuestions publicQuestions = new PublicQuestions();
                    publicQuestions.setFilePath(question.getFilePath()); // 파일 경로
                    publicQuestions.setFileName(question.getFileName()); // 파일 이름
                    publicQuestions.setQualificationDetails(qualificationDetails);
                    return publicQuestions;
                }).collect(Collectors.toList());
        qualificationDetails.setPublicQuestions(publicQuestionsList);
    }

    private void setRecommendBooks(QualificationDetailsApiResponseDTO dto, QualificationDetails qualificationDetails) {
        List<RecommendBooks> recommendBooksList = Optional.ofNullable(dto.getBooks()).orElse(Collections.emptyList()).stream()
                .map(books -> {
                    RecommendBooks recommendBooks = new RecommendBooks();
                    recommendBooks.setTitle(books.getTitle()); // 제목
                    recommendBooks.setAuthors(String.join(", ", books.getAuthors())); // 저자
                    recommendBooks.setPublisher(books.getPublisher()); // 출판사
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(books.getDatetime());
                    recommendBooks.setDate(zonedDateTime.toLocalDate()); // 출판일
                    recommendBooks.setPrice(books.getPrice()); // 정가
                    recommendBooks.setSalePrice((books.getSale_price() == -1 || books.getSale_price() == books.getPrice()) ? null : books.getSale_price()); // 판매가
                    recommendBooks.setThumbnail(books.getThumbnail()); // 썸네일
                    recommendBooks.setUrl(books.getUrl()); // 링크
                    recommendBooks.setQualificationDetails(qualificationDetails);
                    return recommendBooks;
                }).collect(Collectors.toList());
        qualificationDetails.setRecommendBooks(recommendBooksList);
    }
}