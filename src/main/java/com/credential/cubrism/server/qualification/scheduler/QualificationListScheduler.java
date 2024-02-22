package com.credential.cubrism.server.qualification.scheduler;

import com.credential.cubrism.server.qualification.service.QualificationListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class QualificationListScheduler {
    private final QualificationListService qualificationListService;

    @Autowired
    public QualificationListScheduler(QualificationListService qualificationListService) {
        this.qualificationListService = qualificationListService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void saveQualificationListData() {
        qualificationListService.getQualificationList();
    }
}