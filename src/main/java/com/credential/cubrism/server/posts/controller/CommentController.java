package com.credential.cubrism.server.posts.controller;

import com.credential.cubrism.server.common.dto.ResultDTO;
import com.credential.cubrism.server.posts.dto.CommentAddPostDTO;
import com.credential.cubrism.server.posts.dto.CommentUpdatePostDTO;
import com.credential.cubrism.server.posts.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<?> addComment(
            @RequestBody CommentAddPostDTO dto,
            Authentication authentication
    ) {
        try {
            commentService.addComment(dto, authentication);
            return ResponseEntity.ok().body(new ResultDTO(true, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResultDTO(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResultDTO(false, e.getMessage()));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateComment(
            @RequestBody CommentUpdatePostDTO dto,
            Authentication authentication
    ) {
        try {
            commentService.updateComment(dto, authentication);
            return ResponseEntity.ok().body(new ResultDTO(true, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResultDTO(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResultDTO(false, e.getMessage()));
        }
    }
}
