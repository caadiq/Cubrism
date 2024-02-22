package com.credential.cubrism.server.Posts;

import com.credential.cubrism.server.Jwt.LoginRequest;
import com.credential.cubrism.server.Jwt.PrincipalDetails;
import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserRepository userRepository;

    @PostMapping("/write")
    @ResponseBody
    public String writeBoard(@RequestBody PostCreateRequest req, Authentication auth) {
        try {
            postService.writeBoard(req, auth);
            return "Success";
        } catch (Throwable t) {
            return String.format("error : %s", t.getMessage());
        }
    }

    @GetMapping("/post-titles")
    @ResponseBody
    public List<String> getPostTitles() {
        return postService.getAllPostTitles();
    }

    @GetMapping("/my-post-titles")
    @ResponseBody
    public List<String> getMyPostTitles(Authentication auth) {
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
        UUID uuid = principalDetails.getUser().getUuid();
        return postService.getAllPostTitlesByUuid(uuid);
    }
}
