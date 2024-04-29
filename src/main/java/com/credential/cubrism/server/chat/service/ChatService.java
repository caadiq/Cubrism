package com.credential.cubrism.server.chat.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.chat.dto.request.ChatRequest;
import com.credential.cubrism.server.chat.dto.response.ChatResponse;
import com.credential.cubrism.server.chat.model.ChatMessage;
import com.credential.cubrism.server.chat.repository.ChatMessageRepository;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public List<ChatResponse> getAllByCrewId(Long studygroupId) {
        return chatMessageRepository.findAllByStudyGroupId(studygroupId);
    }

    public ChatResponse save(ChatRequest chatRequest, Long studygroupId, Map<String, Object> simpSessionAttributes) {
        ChatMessage newChatMessage = new ChatMessage();
        newChatMessage.setStudyGroupId(studygroupId);
        newChatMessage.setSenderId(chatRequest.getUserId());
        newChatMessage.setContent(chatRequest.getContent());

        ChatMessage savedChatMessage = chatMessageRepository.save(newChatMessage);

        Users sender = userRepository.findById(savedChatMessage.getSenderId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setUserId(savedChatMessage.getSenderId());
        chatResponse.setContent(savedChatMessage.getContent());
        chatResponse.setCreatedAt(savedChatMessage.getCreatedAt());
        chatResponse.setUsername(sender.getNickname());
        chatResponse.setProfileImgUrl(sender.getImageUrl());
        chatResponse.setId(savedChatMessage.getChatMessageId());
        return chatResponse;
    }
}
