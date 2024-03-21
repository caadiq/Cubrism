package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.posts.dto.CommentAddDto;
import com.credential.cubrism.server.posts.dto.CommentDeleteDto;
import com.credential.cubrism.server.posts.dto.CommentUpdateDto;
import com.credential.cubrism.server.posts.entity.Comments;
import com.credential.cubrism.server.posts.entity.Posts;
import com.credential.cubrism.server.posts.repository.CommentRepository;
import com.credential.cubrism.server.posts.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private final SecurityUtil securityUtil;

    // 댓글 추가
    @Transactional
    public ResponseEntity<MessageDto> addComment(CommentAddDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        Posts post = postRepository.findByPostId(dto.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comments comment = new Comments();
        comment.setPost(post);
        comment.setUser(currentUser);
        comment.setContent(dto.getContent());
        commentRepository.save(comment);
        
        return ResponseEntity.ok().body(new MessageDto("댓글을 추가했습니다."));
    }

    // 댓글 삭제
    @Transactional
    public ResponseEntity<MessageDto> deleteComment(CommentDeleteDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        Comments comment = commentRepository.findByPostIdAndCommentId(dto.getPostId(), dto.getCommentId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getUuid().equals(currentUser.getUuid())) {
            throw new CustomException(ErrorCode.DELETE_DENIED);
        }

        commentRepository.delete(comment);

        return ResponseEntity.ok().body(new MessageDto("댓글을 삭제했습니다."));
    }

    // 댓글 수정
    @Transactional
    public ResponseEntity<MessageDto> updateComment(CommentUpdateDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        Comments comment = commentRepository.findByCommentId(dto.getCommentId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getUuid().equals(currentUser.getUuid())) {
            throw new CustomException(ErrorCode.UPDATE_DENIED);
        }

        if (!comment.getPost().getPostId().equals(dto.getPostId())) {
            throw new CustomException(ErrorCode.INVALID_POST_AND_COMMENT);
        }

        comment.setContent(dto.getContent());
        commentRepository.save(comment);

        return ResponseEntity.ok().body(new MessageDto("댓글을 수정했습니다."));
    }
}