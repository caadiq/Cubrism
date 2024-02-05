package com.credential.cubrism.server.qualification.scheduler;

import com.credential.cubrism.server.qualification.service.QualificationDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class QualificationDetailsScheduler {
    private final QualificationDetailsService qualificationDetailsService;

    @Autowired
    public QualificationDetailsScheduler(QualificationDetailsService QualificationDetailsService) {
        this.qualificationDetailsService = QualificationDetailsService;
    }

    @Scheduled(cron = "0 9 * * * *") // 임시 스케줄러
    public void saveQualificationListData() {
        qualificationDetailsService.saveQualificationDetailsData();
    }
}