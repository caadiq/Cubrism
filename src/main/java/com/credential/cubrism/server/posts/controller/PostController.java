package com.credential.cubrism.server.posts.controller;

import com.credential.cubrism.server.common.dto.ErrorDTO;
import com.credential.cubrism.server.common.dto.ResultDTO;
import com.credential.cubrism.server.posts.dto.PostAddPostDTO;
import com.credential.cubrism.server.posts.dto.PostDeletePostDTO;
import com.credential.cubrism.server.posts.dto.PostUpdatePostDTO;
import com.credential.cubrism.server.posts.service.CategoryService;
import com.credential.cubrism.server.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final CategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<?> addPost(
            @RequestBody PostAddPostDTO dto,
            Authentication authentication
    ) {
        try {
            postService.addPost(dto, authentication);
            return ResponseEntity.ok().body(new ResultDTO(true, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResultDTO(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResultDTO(false, e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deletePost(
            @RequestBody PostDeletePostDTO dto,
            Authentication authentication
    ) {
        try {
            postService.deletePost(dto, authentication);
            return ResponseEntity.ok().body(new ResultDTO(true, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResultDTO(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResultDTO(false, e.getMessage()));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updatePost(
            @RequestBody PostUpdatePostDTO dto,
            Authentication authentication
    ) {
        try {
            postService.updatePost(dto, authentication);
            return ResponseEntity.ok().body(new ResultDTO(true, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResultDTO(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResultDTO(false, e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> postList(
            @RequestParam(required = false) String boardName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        try {
            if (boardName == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("'boardName' 파라미터가 필요합니다."));
            }

            limit = Math.max(1, Math.min(limit, 50)); // 한 페이지의 게시글 수를 1~50 사이로 제한
            Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending()); // 페이징 처리 (날짜순으로 정렬)

            return ResponseEntity.ok().body(postService.postList(pageable, boardName));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO(e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> myPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            Authentication authentication
    ) {
        try {
            limit = Math.max(1, Math.min(limit, 50));
            Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending());

            return ResponseEntity.ok().body(postService.myPostList(pageable, authentication));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO(e.getMessage()));
        }
    }

    @GetMapping("/view")
    public ResponseEntity<?> postView(
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) String boardName
    ) {
        try {
            if (postId == null || boardName == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("'postId'와 'boardName' 파라미터가 필요합니다."));
            }

            return ResponseEntity.ok().body(postService.postView(postId, boardName));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO(e.getMessage()));
        }
    }

    @GetMapping("/category")
    public ResponseEntity<?> category(
            @RequestParam(required = false) String search
    ) {
        try {
            return ResponseEntity.ok().body(categoryService.categoryList(search));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDTO(e.getMessage()));
        }
    }
}
