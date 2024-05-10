package com.credential.cubrism.server.chat.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.chat.dto.ChatRequestDto;
import com.credential.cubrism.server.chat.dto.ChatResponseDto;
import com.credential.cubrism.server.chat.entity.ChatMessage;
import com.credential.cubrism.server.chat.repository.ChatMessageRepository;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ResponseEntity<List<ChatResponseDto>> getAllByStudyGroupID(Long studygroupId) {
        List<ChatMessage> messages = chatMessageRepository.findAllByStudyGroupIdOrderByCreatedAtAsc(studygroupId);

        LocalDateTime previousDate = null;
        List<ChatResponseDto> responses = new ArrayList<>();

        for (ChatMessage message : messages) {
            Users sender = userRepository.findById(message.getSenderId()).orElse(null);
            ChatResponseDto response = createChatResponse(message, sender);

            LocalDateTime currentDate = message.getCreatedAt().toLocalDate().atStartOfDay();
            if (!currentDate.equals(previousDate)) {
                response.setIsDateHeader(true);
                previousDate = currentDate;
            } else {
                response.setIsDateHeader(false);
            }

            responses.add(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @Transactional
    public ChatResponseDto save(ChatRequestDto chatRequestDto, Long studygroupId, Map<String, Object> simpSessionAttributes) {
        Users user = userRepository.findByEmail(chatRequestDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChatMessage newChatMessage = new ChatMessage();
        newChatMessage.setStudyGroupId(studygroupId);
        newChatMessage.setSenderId(user.getUuid());
        newChatMessage.setContent(chatRequestDto.getContent());

        ChatMessage savedChatMessage = chatMessageRepository.save(newChatMessage);

        Users sender = userRepository.findById(savedChatMessage.getSenderId()).orElse(null);

        return createChatResponse(savedChatMessage, sender);
    }

    private ChatResponseDto createChatResponse(ChatMessage message, Users sender) {
        ChatResponseDto response = new ChatResponseDto();
        response.setContent(message.getContent());
        response.setCreatedAt(message.getCreatedAt());
        if (sender != null) {
            response.setEmail(sender.getEmail());
            response.setUsername(sender.getNickname());
            response.setProfileImgUrl(sender.getImageUrl());
        }
        response.setId(message.getChatMessageId());
        return response;
    }
}
