package com.credential.cubrism.server.chat.repository;

import com.credential.cubrism.server.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByStudyGroupIdOrderByCreatedAtAsc(Long studygroupId);
}
