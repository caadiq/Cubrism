package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.authentication.utils.AuthenticationUtil;
import com.credential.cubrism.server.posts.dto.CommentAddPostDTO;
import com.credential.cubrism.server.posts.dto.CommentUpdatePostDTO;
import com.credential.cubrism.server.posts.model.Comments;
import com.credential.cubrism.server.posts.model.Posts;
import com.credential.cubrism.server.posts.repository.CommentRepository;
import com.credential.cubrism.server.posts.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addComment(CommentAddPostDTO dto, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);

        Posts post = postRepository.findByPostId(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        Comments comment = new Comments();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(dto.getContent());
        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(CommentUpdatePostDTO dto, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);

        Comments comment = commentRepository.findByCommentId(dto.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getUser().getUuid().equals(user.getUuid())) {
            throw new IllegalArgumentException("본인만 수정할 수 있습니다.");
        }

        if (!comment.getPost().getPostId().equals(dto.getPostId())) {
            throw new IllegalArgumentException("게시글과 댓글이 일치하지 않습니다.");
        }

        comment.setContent(dto.getContent());
        commentRepository.save(comment);
    }
}