package com.credential.cubrism.server.chatting.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "ChatMessage")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID chatMessageId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "senderId", nullable = false)
    private UUID senderId;

    @Column(name = "sentTime", nullable = false)
    private LocalDateTime sentTime;

    @Column(name = "studyGroupId", nullable = false)
    private Long studyGroupId;

}
