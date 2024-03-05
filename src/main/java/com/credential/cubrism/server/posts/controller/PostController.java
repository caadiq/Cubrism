package com.credential.cubrism.server.posts.controller;

import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.oauth.PrincipalDetails;
import com.credential.cubrism.server.posts.dto.PostResponseDto;
import com.credential.cubrism.server.posts.dto.RegisterPostRequestDTO;
import com.credential.cubrism.server.posts.model.Posts;
import com.credential.cubrism.server.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/register")
    public ResponseEntity<?> registerPost(@RequestBody RegisterPostRequestDTO registerPostRequestDTO, Authentication authentication) {
        try {
            postService.registerPost(registerPostRequestDTO, authentication);
            return ResponseEntity.ok("게시글 등록 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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

        return postService.getAllMyPostTitles(auth);
    }

    @GetMapping("/{postId}")
    @ResponseBody
    public Object writeBoardWithCategory(@PathVariable Long postId, Authentication auth) {
        Posts post = postService.getPostByPostId(postId);

          if (post == null) {
              return "Post not found";
          }

        return new PostResponseDto(post);
    }


//    @GetMapping("/my-post-titles")
//    @ResponseBody
//    public List<String> getMyPostTitles(Authentication auth) {
//        Object principal = auth.getPrincipal();
//        PrincipalDetails principalDetails;
//        if (principal instanceof PrincipalDetails) {
//            principalDetails = (PrincipalDetails) principal;
//        } else if (principal instanceof String) {
//            String email = (String) principal;
//            Users user = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
//            principalDetails = new PrincipalDetails(user, new HashMap<>());
//        } else {
//            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass().getName());
//        }
//        UUID uuid = principalDetails.getUser().getUuid();
//        return postService.getAllPostTitlesByUuid(uuid);
//    }
//
//    @GetMapping("/{category}")
//    @ResponseBody
//    public List<Posts> getPostsByCategory(@PathVariable String category) {
//        return postService.getPostsByCategory(category);
//    }
//
//    @PostMapping("/{category}/write")
//    @ResponseBody
//    public String writeBoardWithCategory(@PathVariable String category, @RequestBody RegisterPostRequestDTO req, Authentication auth) {
//        try {
//            postService.writeBoardWithCategory(req, auth, category);
//            return "Success(write with category)";
//        } catch (Throwable t) {
//            return String.format("error : %s", t.getMessage());
//        }
//    }
//
//    @GetMapping("/{category}/{postId}")
//    @ResponseBody
//    public Object writeBoardWithCategory(@PathVariable String category, @PathVariable Long postId, Authentication auth) {
//        Posts post = postService.getPostByPostId(postId);
//
//          if (post == null) {
//              return "Post not found";
//          }
//
//        if (!post.getCategory().equals(category)) {
//            return "category not match";
//        }
//
//        return post;
//    }
//
//    @PostMapping("/addFavoriteCategory")
//    @ResponseBody
//    public Object addFavoriteCategory(@RequestBody AddCategoryDTO addCategoryDTO, Authentication auth) {
//        Object principal = auth.getPrincipal();
//        PrincipalDetails principalDetails;
//        if (principal instanceof PrincipalDetails) {
//            principalDetails = (PrincipalDetails) principal;
//        } else if (principal instanceof String) {
//            String email = (String) principal;
//            Users user = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
//            principalDetails = new PrincipalDetails(user, new HashMap<>());
//            // AddCategoryDTO 객체에서 카테고리를 가져옵니다.
//            String category = addCategoryDTO.getCategory();
//
//            // 사용자의 categories 리스트에 이미 해당 카테고리가 있는지 확인합니다.
//            if (user.getCategories().contains(category)) {
//                // 이미 있다면, 카테고리를 추가하지 않고 메시지를 반환합니다.
//                return "Category already exists in favorites";
//            }
//
//            // 사용자의 categories 리스트에 카테고리를 추가합니다.
//            user.getCategories().add(category);
//
//            // 변경 사항을 저장합니다.
//            userRepository.save(user);
//        } else {
//            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass().getName());
//        }
//
//        return "Success"+" "+addCategoryDTO.getCategory();
//    }
//
//    @PostMapping("/removeFavoriteCategory")
//    @ResponseBody
//    public Object removeFavoriteCategory(@RequestBody AddCategoryDTO addCategoryDTO, Authentication auth) {
//        Object principal = auth.getPrincipal();
//        PrincipalDetails principalDetails;
//        if (principal instanceof PrincipalDetails) {
//            principalDetails = (PrincipalDetails) principal;
//        } else if (principal instanceof String) {
//            String email = (String) principal;
//            Users user = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
//            principalDetails = new PrincipalDetails(user, new HashMap<>());
//            // AddCategoryDTO 객체에서 카테고리를 가져옵니다.
//            String category = addCategoryDTO.getCategory();
//
//            // 사용자의 categories 리스트에 해당 카테고리가 있는지 확인합니다.
//            if (!user.getCategories().contains(category)) {
//                // 없다면, 메시지를 반환합니다.
//                return "Category not found in favorites";
//            }
//
//            // 사용자의 categories 리스트에서 카테고리를 삭제합니다.
//            user.getCategories().remove(category);
//
//            // 변경 사항을 저장합니다.
//            userRepository.save(user);
//        } else {
//            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass().getName());
//        }
//
//        return "Success"+" "+addCategoryDTO.getCategory();
//    }
//
//    @GetMapping("/myFavoriteCategory")
//    @ResponseBody
//    public Object myFavoriteCategory(Authentication auth) {
//        Object principal = auth.getPrincipal();
//        PrincipalDetails principalDetails;
//        if (principal instanceof PrincipalDetails) {
//            principalDetails = (PrincipalDetails) principal;
//        } else if (principal instanceof String) {
//            String email = (String) principal;
//            Users user = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
//            List<String> categories = user.getCategories();
//
//            return categories;
//        } else {
//            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass().getName());
//        }
//
//        return "error";
//    }
//
//    @GetMapping("/myFavoriteCategoryPosts")
//    @ResponseBody
//    public Object myFavoriteCategoryPosts(Authentication auth) {
//        Object principal = auth.getPrincipal();
//        PrincipalDetails principalDetails;
//        if (principal instanceof PrincipalDetails) {
//            principalDetails = (PrincipalDetails) principal;
//        } else if (principal instanceof String) {
//            String email = (String) principal;
//            Users user = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
//            List<String> categories = user.getCategories();
//            List<Posts> posts = postRepository.findByCategoryIn(categories);
//
//            String result = "{";
//            for (Posts post : posts) {
//                result += post.getTitle() + ", ";
//            }
//            result += "}";
//
//            return result; // 나중에 얘 post로 바꾸면 json 형태로 전달될거임
//        } else {
//            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass().getName());
//        }
//
//        return "error";
//    }

}
