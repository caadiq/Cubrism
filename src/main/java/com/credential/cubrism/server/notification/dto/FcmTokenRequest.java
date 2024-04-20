package com.credential.cubrism.server.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FcmTokenRequest {
    private String fcmToken;

    public FcmTokenRequest() {
    }

    public FcmTokenRequest(String fcmToken) {
        this.fcmToken = fcmToken;
    }

}
