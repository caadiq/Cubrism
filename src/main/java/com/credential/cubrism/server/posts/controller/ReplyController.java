package com.credential.cubrism.server.posts.controller;

import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.posts.dto.ReplyAddDto;
import com.credential.cubrism.server.posts.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/replies")
public class ReplyController {
    private final ReplyService replyService;

    @PostMapping("/add") // 대댓글 추가
    public ResponseEntity<MessageDto> addReply(@RequestBody ReplyAddDto dto) {
        return replyService.addReply(dto);
    }
}
