package com.credential.cubrism.server.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FcmMessageDto {
    private Message message;

    @Getter
    @AllArgsConstructor
    public static class Message {
        private String token;
        private Data data;
    }

    @Getter
    @AllArgsConstructor
    public static class Data {
        private String title;
        private String body;
    }
}
