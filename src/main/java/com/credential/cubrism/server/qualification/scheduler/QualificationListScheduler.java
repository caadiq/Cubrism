package com.credential.cubrism.server.qualification.scheduler;

import com.credential.cubrism.server.qualification.service.QualificationListService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QualificationListScheduler {
    private final QualificationListService qualificationListService;

    @Scheduled(cron = "0 0 0 * * *")
    public void saveQualificationListData() {
        qualificationListService.getQualificationList();
    }
}