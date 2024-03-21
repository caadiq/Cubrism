package com.credential.cubrism.server.qualification.scheduler;

import com.credential.cubrism.server.qualification.service.QualificationCrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QualificationCrawlingScheduler {
    private final QualificationCrawlingService qualificationDetailsService;

    @Scheduled(cron = "0 0 0 * * *") // 매일 0시 0분 0초에 실행
    public void saveQualificationList() {
        qualificationDetailsService.getQualificationList();
    }

    @Scheduled(cron = "0 5 0 * * *") // 매일 0시 5분 0초에 실행
    public void saveQualificationDetails() {
        qualificationDetailsService.getQualificationDetails();
    }
}