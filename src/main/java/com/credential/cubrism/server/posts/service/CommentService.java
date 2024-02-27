package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.authentication.model.Comment;
import com.credential.cubrism.server.authentication.model.Posts;
import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.posts.dto.CommentCreateRequest;
import com.credential.cubrism.server.posts.repository.CommentRepository;
import com.credential.cubrism.server.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository boardRepository;
    private final UserRepository userRepository;

    public void writeComment(Long boardId, CommentCreateRequest req, String loginId) {
        Posts board = boardRepository.findByPostId(boardId);
        Users user = userRepository.findByEmail(loginId).get();
        Comment comment =req.toEntity(board, user);
        commentRepository.save(comment);
    }

    public List<Comment> findAll(Long boardId) {
        return commentRepository.findAllByPost_PostId(boardId);
    }

//    @Transactional
//    public Long editComment(Long commentId, String newBody, String loginId) {
//        Optional<Comment> optComment = commentRepository.findById(commentId);
//        Optional<Users> optUser = userRepository.findByEmail(loginId);
//        if (optComment.isEmpty() || optUser.isEmpty() || !optComment.get().getUser().equals(optUser.get())) {
//            return null;
//        }
//
//        Comment comment = optComment.get();
//        comment.update(newBody);
//
//        return comment.getPost().getPostId();
//    }

}

