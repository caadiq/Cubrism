package com.credential.cubrism.server.notification.service;

import com.credential.cubrism.server.notification.dto.FcmMessageDto;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/" +
            "cubrism-79e41/messages:send";
    private final ObjectMapper objectMapper;

    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeMessage(targetToken, title, body);

        WebClient webClient = WebClient.create();

        String response = webClient.post()
                .uri(API_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .header(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .body(BodyInserters.fromValue(message))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println(response);
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonParseException, JsonProcessingException {
        FcmMessageDto fcmMessage = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessageDto.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/firebase_service_key.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
