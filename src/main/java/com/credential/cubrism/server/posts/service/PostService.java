package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.oauth.PrincipalDetails;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.posts.dto.RegisterPostRequestDTO;
import com.credential.cubrism.server.posts.model.Board;
import com.credential.cubrism.server.posts.model.Posts;
import com.credential.cubrism.server.posts.repository.BoardRepository;
import com.credential.cubrism.server.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerPost(RegisterPostRequestDTO registerPostRequestDTO, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        Users user;
        if (principal instanceof PrincipalDetails principalDetails) {
            user = principalDetails.user();
        } else if (principal instanceof String email) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + principal));
        } else {
            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass().getName());
        }

        Board board = boardRepository.findByBoardName(registerPostRequestDTO.getBoardName())
                .orElseThrow(() -> new IllegalArgumentException("Board not found with name : " + registerPostRequestDTO.getBoardName()));

        Posts post = new Posts();
        post.setBoard(board);
        post.setUser(user);
        post.setTitle(registerPostRequestDTO.getTitle());
        post.setContent(registerPostRequestDTO.getContent());

        postRepository.save(post);
    }
}
