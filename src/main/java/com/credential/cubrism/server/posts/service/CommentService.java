package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.notification.service.FirebaseCloudMessageService;
import com.credential.cubrism.server.posts.dto.CommentAddDto;
import com.credential.cubrism.server.posts.dto.CommentUpdateDto;
import com.credential.cubrism.server.posts.dto.ReplyAddDto;
import com.credential.cubrism.server.posts.dto.ReplyUpdateDto;
import com.credential.cubrism.server.posts.entity.Comments;
import com.credential.cubrism.server.posts.entity.Posts;
import com.credential.cubrism.server.posts.entity.Replies;
import com.credential.cubrism.server.posts.repository.CommentRepository;
import com.credential.cubrism.server.posts.repository.PostRepository;
import com.credential.cubrism.server.posts.repository.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    private final SecurityUtil securityUtil;

    // 댓글 추가
    @Transactional
    public ResponseEntity<MessageDto> addComment(CommentAddDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        // 게시글
        Posts post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comments comment = new Comments();
        comment.setPost(post);
        comment.setUser(currentUser);
        comment.setContent(dto.getContent());
        commentRepository.save(comment);

        // 게시글 작성자와 댓글 작성자가 다른 경우에만 알림을 보냅니다.
        if (!post.getUser().getUuid().equals(currentUser.getUuid())) {
            String fcmToken = post.getUser().getFcmToken();

            // FCM 토큰이 있는 경우에만 알림을 보냅니다.
            if (fcmToken != null) {
                // 알림 메시지를 생성합니다.
                String title = "새로운 댓글 알림";
                String body = currentUser.getNickname() + "님이 댓글을 남겼습니다: " + comment.getContent();

                // 알림을 보냅니다.
                try {
                    firebaseCloudMessageService.sendMessageTo(fcmToken, title, body);
                } catch (IOException ignored) {

                }
            }
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDto("댓글을 추가했습니다."));
    }

    // 댓글 삭제
    @Transactional
    public ResponseEntity<MessageDto> deleteComment(Long commentId) {
        Users currentUser = securityUtil.getCurrentUser();

        // 갯글
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 작성자 본인인지 확인
        if (!comment.getUser().getUuid().equals(currentUser.getUuid())) {
            throw new CustomException(ErrorCode.DELETE_DENIED);
        }

        commentRepository.delete(comment);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("댓글을 삭제했습니다."));
    }

    // 댓글 수정
    @Transactional
    public ResponseEntity<MessageDto> updateComment(Long commentId, CommentUpdateDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        // 댓글
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 작성자 본인인지 확인
        if (!comment.getUser().getUuid().equals(currentUser.getUuid())) {
            throw new CustomException(ErrorCode.UPDATE_DENIED);
        }

        comment.setContent(dto.getContent());
        commentRepository.save(comment);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDto("댓글을 수정했습니다."));
    }

    // 대댓글 추가
    @Transactional
    public ResponseEntity<MessageDto> addReply(ReplyAddDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        // 댓글
        Comments comment = commentRepository.findById(dto.getCommentId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Replies reply = new Replies();
        reply.setComment(comment);
        reply.setUser(currentUser);
        reply.setContent(dto.getContent());
        replyRepository.save(reply);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDto("대댓글을 추가했습니다."));
    }

    // 대댓글 삭제
    @Transactional
    public ResponseEntity<MessageDto> deleteReply(Long replyId) {
        Users currentUser = securityUtil.getCurrentUser();

        // 대댓글
        Replies reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));

        // 작성자 본인인지 확인
        if (!reply.getUser().getUuid().equals(currentUser.getUuid())) {
            throw new CustomException(ErrorCode.DELETE_DENIED);
        }

        replyRepository.delete(reply);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("대댓글을 삭제했습니다."));
    }

    // 대댓글 수정
    @Transactional
    public ResponseEntity<MessageDto> updateReply(Long replyId, ReplyUpdateDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        // 대댓글
        Replies reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));

        // 작성자 본인인지 확인
        if (!reply.getUser().getUuid().equals(currentUser.getUuid())) {
            throw new CustomException(ErrorCode.UPDATE_DENIED);
        }

        reply.setContent(dto.getContent());
        replyRepository.save(reply);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDto("대댓글을 수정했습니다."));
    }
}