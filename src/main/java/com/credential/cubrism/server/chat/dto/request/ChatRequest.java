package com.credential.cubrism.server.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatRequest {
    MessageType type;
    Long userId;
    String content;
}
