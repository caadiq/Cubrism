package com.credential.cubrism.server.chat.controller;


import com.credential.cubrism.server.chat.dto.ChatRequestDto;
import com.credential.cubrism.server.chat.dto.ChatResponseDto;
import com.credential.cubrism.server.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @ResponseBody
    @GetMapping(value = "/studygroup/{studygroupId}/chats")
    public ResponseEntity<List<ChatResponseDto>> getChattingList(@PathVariable(name = "studygroupId") Long studygroupId) {
        return chatService.getAllByStudyGroupID(studygroupId);
    }

    @MessageMapping("/sendmessage/{studygroupId}")
    @SendTo("/topic/public/{studygroupId}")
    public ChatResponseDto sendMessage(@DestinationVariable Long studygroupId,
                                       @Header("simpSessionAttributes") Map<String, Object> simpSessionAttributes,
                                       @Payload ChatRequestDto chatRequestDto
    ) {
        return chatService.save(chatRequestDto, studygroupId, simpSessionAttributes);
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String topic = headerAccessor.getDestination();
        log.info("구독 topic: {}", topic);
    }
}