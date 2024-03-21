package com.credential.cubrism.server.posts.controller;

import com.credential.cubrism.server.posts.dto.CommentAddDto;
import com.credential.cubrism.server.posts.dto.CommentDeleteDto;
import com.credential.cubrism.server.posts.dto.CommentUpdateDto;
import com.credential.cubrism.server.posts.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/add") // 댓글 추가
    public ResponseEntity<?> addComment(@RequestBody CommentAddDto dto) {
        return commentService.addComment(dto);
    }

    @PostMapping("/delete") // 댓글 삭제
    public ResponseEntity<?> deleteComment(@RequestBody CommentDeleteDto dto) {
        return commentService.deleteComment(dto);
    }

    @PostMapping("/update") // 댓글 수정
    public ResponseEntity<?> updateComment(@RequestBody CommentUpdateDto dto) {
        return commentService.updateComment(dto);
    }
}
