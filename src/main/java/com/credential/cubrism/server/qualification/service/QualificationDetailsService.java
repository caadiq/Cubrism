package com.credential.cubrism.server.qualification.service;

import com.credential.cubrism.server.qualification.dto.QualificationDetailsApiGetDTO;
import com.credential.cubrism.server.qualification.dto.QualificationDetailsPostDTO;
import com.credential.cubrism.server.qualification.model.*;
import com.credential.cubrism.server.qualification.repository.QualificationDetailsRepository;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QualificationDetailsService {
    private final QualificationListRepository qualificationListRepository;
    private final QualificationDetailsRepository qualificationDetailsRepository;
    private final WebClient webClient;

    public void getQualificationDetails() {
        String url = "http://localhost:8000/qualification";
        List<QualificationList> qualificationList = qualificationListRepository.findAll();
        List<QualificationDetailsPostDTO> qualificationDetailsPostDTO = qualificationList.stream()
                .map(qualification -> new QualificationDetailsPostDTO(qualification.getCode(), qualification.getName()))
                .collect(Collectors.toList());

        webClient.post()
                .uri(url)
                .bodyValue(qualificationDetailsPostDTO)
                .retrieve()
                .bodyToFlux(QualificationDetailsApiGetDTO.class)
                .retryWhen(Retry.backoff(5, Duration.ofMinutes(1)).maxBackoff(Duration.ofMinutes(2)))
                .collectList()
                .subscribe(this::saveQualificationDetails);
    }

    private void saveQualificationDetails(List<QualificationDetailsApiGetDTO> dtoList) {
        dtoList.forEach(dto -> qualificationDetailsRepository.deleteByCode(dto.getCode()));

        List<QualificationDetails> allQualificationDetails = dtoList.stream()
                .flatMap(dto -> qualificationListRepository.findById(dto.getCode()).stream()
                        .map(qualificationList -> setQualificationDetails(dto)))
                .collect(Collectors.toList());

        qualificationDetailsRepository.saveAll(allQualificationDetails);
    }

    private QualificationDetails setQualificationDetails(QualificationDetailsApiGetDTO dto) {
        QualificationDetails details = new QualificationDetails();
        details.setCode(dto.getCode());
        details.setTendency(dto.getTendency());
        details.setAcquisition(dto.getAcquisition());

        setExamSchedules(dto, details);
        setExamFees(dto, details);
        setExamStandards(dto, details);
        setPublicQuestions(dto, details);
        setRecommendBooks(dto, details);

        return details;
    }

    private void setExamSchedules(QualificationDetailsApiGetDTO dto, QualificationDetails qualificationDetails) {
        Optional.ofNullable(dto.getSchedule()).ifPresent(scheduleList -> {
            List<ExamSchedules> examSchedulesList = scheduleList.stream()
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
        });
    }

    private void setExamFees(QualificationDetailsApiGetDTO dto, QualificationDetails qualificationDetails) {
        ExamFees examFees = new ExamFees();
        examFees.setCode(dto.getCode());
        examFees.setWrittenFee(Optional.ofNullable(dto.getFee()).map(QualificationDetailsApiGetDTO.Fee::getWrittenFee).orElse(null)); // 필기 수수료
        examFees.setPracticalFee(Optional.ofNullable(dto.getFee()).map(QualificationDetailsApiGetDTO.Fee::getPracticalFee).orElse(null)); // 실기 수수료
        qualificationDetails.setExamFees(examFees);
    }

    private void setExamStandards(QualificationDetailsApiGetDTO dto, QualificationDetails qualificationDetails) {
        Optional.ofNullable(dto.getStandard()).ifPresent(standardList -> {
            List<ExamStandards> examStandardsList = standardList.stream()
                    .map(standard -> {
                        ExamStandards examStandards = new ExamStandards();
                        examStandards.setFilePath(standard.getFilePath()); // 파일 경로
                        examStandards.setFileName(standard.getFileName()); // 파일 이름
                        examStandards.setQualificationDetails(qualificationDetails);
                        return examStandards;
                    }).collect(Collectors.toList());
            qualificationDetails.setExamStandards(examStandardsList);
        });
    }

    private void setPublicQuestions(QualificationDetailsApiGetDTO dto, QualificationDetails qualificationDetails) {
        Optional.ofNullable(dto.getQuestion()).ifPresent(standardList -> {
            List<PublicQuestions> publicQuestionsList = standardList.stream()
                    .map(question -> {
                        PublicQuestions publicQuestions = new PublicQuestions();
                        publicQuestions.setFilePath(question.getFilePath()); // 파일 경로
                        publicQuestions.setFileName(question.getFileName()); // 파일 이름
                        publicQuestions.setQualificationDetails(qualificationDetails);
                        return publicQuestions;
                    }).collect(Collectors.toList());
            qualificationDetails.setPublicQuestions(publicQuestionsList);
        });
    }

    private void setRecommendBooks(QualificationDetailsApiGetDTO dto, QualificationDetails qualificationDetails) {
        Optional.ofNullable(dto.getBooks()).ifPresent(books -> {
            List<RecommendBooks> recommendBooksList = books.stream()
                    .map(book -> {
                        RecommendBooks recommendBooks = new RecommendBooks();
                        recommendBooks.setTitle(book.getTitle()); // 제목
                        recommendBooks.setAuthors(String.join(", ", book.getAuthors())); // 저자
                        recommendBooks.setPublisher(book.getPublisher()); // 출판사
                        ZonedDateTime zonedDateTime = ZonedDateTime.parse(book.getDatetime());
                        recommendBooks.setDate(zonedDateTime.toLocalDate()); // 출판일
                        recommendBooks.setPrice(book.getPrice()); // 정가
                        recommendBooks.setSalePrice((book.getSale_price() == -1 || book.getSale_price() == book.getPrice()) ? null : book.getSale_price()); // 판매가
                        recommendBooks.setThumbnail(book.getThumbnail()); // 썸네일
                        recommendBooks.setUrl(book.getUrl()); // 링크
                        recommendBooks.setQualificationDetails(qualificationDetails);
                        return recommendBooks;
                    }).collect(Collectors.toList());
            qualificationDetails.setRecommendBooks(recommendBooksList);
        });
    }
}