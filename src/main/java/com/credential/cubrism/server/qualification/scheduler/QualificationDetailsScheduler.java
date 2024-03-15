package com.credential.cubrism.server.qualification.scheduler;

import com.credential.cubrism.server.qualification.service.QualificationDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QualificationDetailsScheduler {
    private final QualificationDetailsService qualificationDetailsService;

    @Scheduled(cron = "0 5 0 * * *")
    public void saveQualificationDetailsData() {
        qualificationDetailsService.getQualificationDetails();
    }
}