package com.credential.cubrism.server.Posts;

import com.credential.cubrism.server.Jwt.PrincipalDetails;
import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long writeBoard(PostCreateRequest req, Authentication auth) throws IOException {
        Object principal = auth.getPrincipal();
        PrincipalDetails principalDetails;
        if (principal instanceof PrincipalDetails) {
            principalDetails = (PrincipalDetails) principal;
        } else if (principal instanceof String) {
            String email = (String) principal;
            Users user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
            principalDetails = new PrincipalDetails(user, new HashMap<>());

        } else {
            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass().getName());
        }

        Posts post = req.toEntity(principalDetails.getUser());



        Posts savedBoard = postRepository.save(post);

        return savedBoard.getPostId();
    }

    public List<String> getAllPostTitles() {
        return postRepository.findAllTitles();
    }

    public List<String> getAllPostTitlesByUuid(UUID uuid) {
        return postRepository.findAllTitlesByUuid(uuid);
    }

}
