package com.credential.cubrism.server.chat.repository;

import com.credential.cubrism.server.chat.dto.response.ChatResponse;
import com.credential.cubrism.server.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatResponse> findAllByStudyGroupId(Long studygroupId);
}
