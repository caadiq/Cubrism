package com.credential.cubrism.server.notification.utils;

import com.credential.cubrism.server.notification.dto.FcmMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FcmUtils {

    @Value("${fcm.api.url}")
    private String fcmApiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public void sendMessageTo(String targetToken, String title, String body) {
        String message = makeMessage(targetToken, title, body);
        String accessToken = getAccessToken();

        if (message != null && accessToken != null) {
            webClient.post()
                    .uri(fcmApiUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .body(BodyInserters.fromValue(message))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }
    }

    private String makeMessage(String targetToken, String title, String body) {
        try {
            FcmMessageDto fcmMessage = new FcmMessageDto(new FcmMessageDto.Message(
                    targetToken,
                    new FcmMessageDto.Data(title, body))
            );

            return objectMapper.writeValueAsString(fcmMessage);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String getAccessToken() {
        String firebaseConfigPath = "firebase/firebase_service_key.json";

        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            return null;
        }
    }
}
