package com.credential.cubrism.server.posts.controller;

import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.posts.dto.CommentAddDto;
import com.credential.cubrism.server.posts.dto.CommentUpdateDto;
import com.credential.cubrism.server.posts.dto.ReplyAddDto;
import com.credential.cubrism.server.posts.dto.ReplyUpdateDto;
import com.credential.cubrism.server.posts.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comment") // 댓글 추가
    public ResponseEntity<?> addComment(@RequestBody CommentAddDto dto) {
        return commentService.addComment(dto);
    }

    @DeleteMapping("/comment/{commentId}") // 댓글 삭제
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }

    @PutMapping("/comment/{commentId}") // 댓글 수정
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody CommentUpdateDto dto) {
        return commentService.updateComment(commentId, dto);
    }

    @PostMapping("/reply") // 대댓글 추가
    public ResponseEntity<MessageDto> addReply(@RequestBody ReplyAddDto dto) {
        return commentService.addReply(dto);
    }

    @DeleteMapping("/reply/{replyId}") // 대댓글 삭제
    public ResponseEntity<MessageDto> deleteReply(@PathVariable Long replyId) {
        return commentService.deleteReply(replyId);
    }

    @PutMapping("/reply/{replyId}") // 대댓글 수정
    public ResponseEntity<MessageDto> updateReply(@PathVariable Long replyId, @RequestBody ReplyUpdateDto dto) {
        return commentService.updateReply(replyId, dto);
    }
}
