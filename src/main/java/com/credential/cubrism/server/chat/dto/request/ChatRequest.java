package com.credential.cubrism.server.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ChatRequest {
    String email;
    String content;
}
