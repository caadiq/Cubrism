package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.authentication.utils.AuthenticationUtil;
import com.credential.cubrism.server.posts.dto.RegisterPostRequestDTO;
import com.credential.cubrism.server.posts.model.Board;
import com.credential.cubrism.server.posts.model.Posts;
import com.credential.cubrism.server.posts.repository.BoardRepository;
import com.credential.cubrism.server.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerPost(RegisterPostRequestDTO registerPostRequestDTO, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);

        Board board = boardRepository.findByBoardName(registerPostRequestDTO.getBoardName())
                .orElseThrow(() -> new IllegalArgumentException("Board not found with name : " + registerPostRequestDTO.getBoardName()));

        Posts post = new Posts();
        post.setBoard(board);
        post.setUser(user);
        post.setTitle(registerPostRequestDTO.getTitle());
        post.setContent(registerPostRequestDTO.getContent());

        postRepository.save(post);
    }

    public List<String> getAllPostTitles() {
        return postRepository.findAllTitles();
    }

    public List<String> getAllMyPostTitles(Authentication auth) {
        Users user = AuthenticationUtil.getUserFromAuthentication(auth, userRepository);
        UUID uuid = user.getUuid();
        return postRepository.findAllTitlesByUuid(uuid);
    }

    public Posts getPostByPostId(Long postId) {
        return postRepository.findByPostId(postId);
    }


}
