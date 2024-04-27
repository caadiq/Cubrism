package com.credential.cubrism.server.chatting.repository;

import com.credential.cubrism.server.chatting.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
