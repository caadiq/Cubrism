package com.credential.cubrism.server.qualification.service;

import com.credential.cubrism.server.qualification.dto.QualificationListDto;
import com.credential.cubrism.server.qualification.dto.QualificationCrawlingDto;
import com.credential.cubrism.server.qualification.dto.QualificationCrawlingRequestDto;
import com.credential.cubrism.server.qualification.dto.QualificationListRequestDto;
import com.credential.cubrism.server.qualification.entity.*;
import com.credential.cubrism.server.qualification.repository.QualificationDetailsRepository;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QualificationCrawlingService {
    @Value("${open.api.key:}")
    private String apiKey;

    @Value("${open.api.url}")
    private String openApiUrl;

    @Value("${fast.api.url}")
    private String fastApiUrl;

    private final QualificationListRepository qualificationListRepository;
    private final QualificationDetailsRepository qualificationDetailsRepository;

    private final WebClient webClient;

    // 자격증 목록 가져오기
    @Transactional
    @CachePut(value = "qualificationList")
    public List<QualificationListDto> getQualificationList() {
        String url = openApiUrl + apiKey;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(QualificationListRequestDto.class)
                .retryWhen(Retry.backoff(5, Duration.ofMinutes(1)).maxBackoff(Duration.ofMinutes(2)))
                .flatMap(dto -> {
                    List<QualificationList> qualificationLists = dto.getResponse().getBody().getItems().getItem().stream()
                            .filter(item -> "국가기술자격".equals(item.getQualgbnm())) // 국가기술자격만 저장
                            .map(item -> {
                                QualificationList qualificationList = new QualificationList();
                                qualificationList.setCode(item.getJmcd());
                                qualificationList.setName(item.getJmfldnm());
                                qualificationList.setMiddleFieldName(item.getMdobligfldnm());
                                qualificationList.setMajorFieldName(item.getObligfldnm());
                                qualificationList.setQualName(item.getQualgbnm());
                                qualificationList.setSeriesName(item.getSeriesnm());
                                return qualificationList;
                            }).collect(Collectors.toList());

                    qualificationListRepository.saveAll(qualificationLists);

                    return Mono.just(qualificationLists.stream()
                            .map(qualificationList -> new QualificationListDto(
                                    qualificationList.getCode(),
                                    qualificationList.getName()
                            )).collect(Collectors.toList()));
                }).block();
    }


    // 자격증 세부정보 가져오기
    @Transactional
    public void getQualificationDetails() {
        String url = fastApiUrl + "/qualification";
        List<QualificationCrawlingRequestDto> qualificationCrawlingRequestDto = qualificationListRepository.findAll().stream()
                .map(qualification -> new QualificationCrawlingRequestDto(
                        qualification.getCode(),
                        qualification.getName()
                )).toList();

        webClient.post()
                .uri(url)
                .bodyValue(qualificationCrawlingRequestDto)
                .retrieve()
                .bodyToFlux(QualificationCrawlingDto.class)
                .retryWhen(Retry.backoff(5, Duration.ofMinutes(1)).maxBackoff(Duration.ofMinutes(2)))
                .subscribe(this::saveQualificationDetails);
    }

    private void saveQualificationDetails(QualificationCrawlingDto dto) {
        qualificationDetailsRepository.deleteByCode(dto.getCode());
        QualificationDetails qualificationDetails = setQualificationDetails(dto);
        qualificationDetailsRepository.save(qualificationDetails);
    }

    private QualificationDetails setQualificationDetails(QualificationCrawlingDto dto) {
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

    private void setExamSchedules(QualificationCrawlingDto dto, QualificationDetails qualificationDetails) {
        Optional.ofNullable(dto.getSchedule()).ifPresent(scheduleList ->
                qualificationDetails.setExamSchedules(scheduleList.stream()
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
                        }).toList()
                )
        );
    }

    private void setExamFees(QualificationCrawlingDto dto, QualificationDetails qualificationDetails) {
        ExamFees examFees = new ExamFees();
        examFees.setCode(dto.getCode());
        examFees.setWrittenFee(Optional.ofNullable(dto.getFee()).map(QualificationCrawlingDto.Fee::getWrittenFee).orElse(null)); // 필기 수수료
        examFees.setPracticalFee(Optional.ofNullable(dto.getFee()).map(QualificationCrawlingDto.Fee::getPracticalFee).orElse(null)); // 실기 수수료
        qualificationDetails.setExamFees(examFees);
    }

    private void setExamStandards(QualificationCrawlingDto dto, QualificationDetails qualificationDetails) {
        Optional.ofNullable(dto.getStandard()).ifPresent(standardList ->
                qualificationDetails.setExamStandards(standardList.stream()
                        .map(standard -> {
                            ExamStandards examStandards = new ExamStandards();
                            examStandards.setFilePath(standard.getFilePath()); // 파일 경로
                            examStandards.setFileName(standard.getFileName()); // 파일 이름
                            examStandards.setQualificationDetails(qualificationDetails);
                            return examStandards;
                        }).toList()
                )
        );
    }

    private void setPublicQuestions(QualificationCrawlingDto dto, QualificationDetails qualificationDetails) {
        Optional.ofNullable(dto.getQuestion()).ifPresent(questionList ->
                qualificationDetails.setPublicQuestions(questionList.stream()
                        .map(question -> {
                            PublicQuestions publicQuestions = new PublicQuestions();
                            publicQuestions.setFilePath(question.getFilePath()); // 파일 경로
                            publicQuestions.setFileName(question.getFileName()); // 파일 이름
                            publicQuestions.setQualificationDetails(qualificationDetails);
                            return publicQuestions;
                        }).toList()
                )
        );
    }

    private void setRecommendBooks(QualificationCrawlingDto dto, QualificationDetails qualificationDetails) {
        Optional.ofNullable(dto.getBooks()).ifPresent(booksList ->
                qualificationDetails.setRecommendBooks(booksList.stream()
                        .map(book -> {
                            RecommendBooks recommendBooks = new RecommendBooks();
                            recommendBooks.setTitle(book.getTitle()); // 제목
                            recommendBooks.setAuthors(String.join(", ", book.getAuthors())); // 저자
                            recommendBooks.setPublisher(book.getPublisher()); // 출판사
                            ZonedDateTime zonedDateTime = ZonedDateTime.parse(book.getDatetime());
                            recommendBooks.setDate(zonedDateTime.toLocalDate()); // 출판일
                            recommendBooks.setPrice(book.getPrice()); // 정가
                            recommendBooks.setSalePrice((book.getSale_price() == -1 || book.getSale_price() == book.getPrice()) ? null : book.getSale_price()); // 판매가
                            recommendBooks.setThumbnail(book.getThumbnail()); // 책 표지
                            recommendBooks.setUrl(book.getUrl()); // 링크
                            recommendBooks.setQualificationDetails(qualificationDetails);
                            return recommendBooks;
                        }).toList()
                )
        );
    }
}
