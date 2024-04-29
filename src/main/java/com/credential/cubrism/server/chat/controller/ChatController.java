package com.credential.cubrism.server.chat.controller;


import com.credential.cubrism.server.chat.dto.request.ChatRequest;
import com.credential.cubrism.server.chat.dto.response.ChatResponse;
import com.credential.cubrism.server.chat.service.ChatService;
import com.credential.cubrism.server.common.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    @ResponseBody
    @GetMapping(value = "/api/v1/studygroups/{studygroupId}/chats")
    public ResponseEntity<List<ChatResponse>> getChattingList(
            @PathVariable(name = "studygroupId") Long studygroupId) {

        List<ChatResponse> result = chatService.getAllByCrewId(studygroupId);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @MessageMapping("/chat.sendMessage/{studygroupId}")
    @SendTo("/topic/public/{studygroupId}")
    public ChatResponse sendMessage(@DestinationVariable Long studygroupId,
                                    @Header("simpSessionAttributes") Map<String, Object> simpSessionAttributes,
                                    @Payload ChatRequest chatRequest
    ) {
        return chatService.save(chatRequest, studygroupId, simpSessionAttributes);
    }

}