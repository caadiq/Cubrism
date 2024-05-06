package com.credential.cubrism.server.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    UUID id;
    String email;
    String username;
    String profileImgUrl;
    LocalDateTime createdAt;
    String content;
}
