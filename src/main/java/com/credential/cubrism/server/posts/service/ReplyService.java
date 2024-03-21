package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.posts.dto.ReplyAddDto;
import com.credential.cubrism.server.posts.entity.Comments;
import com.credential.cubrism.server.posts.entity.Replies;
import com.credential.cubrism.server.posts.repository.CommentRepository;
import com.credential.cubrism.server.posts.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    private final SecurityUtil securityUtil;

    // 대댓글 추가
    @Transactional
    public ResponseEntity<MessageDto> addReply(ReplyAddDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        Comments comment = commentRepository.findByCommentId(dto.getCommentId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Replies reply = new Replies();
        reply.setComment(comment);
        reply.setUser(currentUser);
        reply.setContent(dto.getContent());
        replyRepository.save(reply);

        return ResponseEntity.ok().body(new MessageDto("대댓글을 추가했습니다."));
    }
}
