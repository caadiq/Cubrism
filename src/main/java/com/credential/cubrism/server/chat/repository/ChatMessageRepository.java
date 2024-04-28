package com.credential.cubrism.server.chat.repository;

import com.credential.cubrism.server.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
