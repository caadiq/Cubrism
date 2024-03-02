package com.credential.cubrism.server.qualification.scheduler;

import com.credential.cubrism.server.qualification.service.QualificationListApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class QualificationListScheduler {
    private final QualificationListApiService qualificationListApiService;

    @Autowired
    public QualificationListScheduler(QualificationListApiService qualificationListApiService) {
        this.qualificationListApiService = qualificationListApiService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void saveQualificationListData() {
        qualificationListApiService.getQualificationList();
    }
}