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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public List<ChatResponse> getAllByStudyGroupID(Long studygroupId) {
        List<ChatMessage> messages = chatMessageRepository.findAllByStudyGroupId(studygroupId);
        List<ChatResponse> responses = messages.stream()
                .map(message -> {
                    Users sender = userRepository.findById(message.getSenderId()).orElse(null);
                    ChatResponse response = new ChatResponse();
                    response.setContent(message.getContent());
                    response.setCreatedAt(message.getCreatedAt());
                    if (sender != null) {
                        response.setEmail(sender.getEmail());
                        response.setUsername(sender.getNickname());
                        response.setProfileImgUrl(sender.getImageUrl());
                    }
                    response.setId(message.getChatMessageId());
                    return response;
                })
                .collect(Collectors.toList());

        return responses;
    }

    public ChatResponse save(ChatRequest chatRequest, Long studygroupId, Map<String, Object> simpSessionAttributes) {
        Optional<Users> userOptional = userRepository.findByEmail(chatRequest.getEmail());
        if (!userOptional.isPresent()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        Users user = userOptional.get();
        ChatMessage newChatMessage = new ChatMessage();
        newChatMessage.setStudyGroupId(studygroupId);
        newChatMessage.setSenderId(user.getUuid());
        newChatMessage.setContent(chatRequest.getContent());

        ChatMessage savedChatMessage = chatMessageRepository.save(newChatMessage);

        Users sender = userRepository.findById(savedChatMessage.getSenderId()).orElse(null);

        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setContent(savedChatMessage.getContent());
        chatResponse.setCreatedAt(savedChatMessage.getCreatedAt());
        if (sender != null) {
            chatResponse.setEmail(sender.getEmail());
            chatResponse.setUsername(sender.getNickname());
            chatResponse.setProfileImgUrl(sender.getImageUrl());
        }
        chatResponse.setId(savedChatMessage.getChatMessageId());
        return chatResponse;
    }
}
