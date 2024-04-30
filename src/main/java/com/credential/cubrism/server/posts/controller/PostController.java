package com.credential.cubrism.server.posts.controller;

import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.posts.dto.*;
import com.credential.cubrism.server.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/post") // 게시글 작성
    public ResponseEntity<MessageDto> addPost(@RequestBody PostAddDto dto) {
        return postService.addPost(dto);
    }

    @DeleteMapping("/post/{postId}") // 게시글 삭제
    public ResponseEntity<MessageDto> deletePost(@PathVariable Long postId) {
        return postService.deletePost(postId);
    }

    @PutMapping("/post/{postId}") // 게시글 수정
    public ResponseEntity<MessageDto> updatePost(@PathVariable Long postId, @RequestBody PostUpdateDto dto) {
        return postService.updatePost(postId, dto);
    }

    @GetMapping("/post/{postId}") // 게시글 보기
    public ResponseEntity<PostViewDto> postView(@PathVariable Long postId) {
        return postService.postView(postId);
    }

    @GetMapping("/posts") // 게시글 목록
    public ResponseEntity<PostListDto> postList(
            @RequestParam(value = "board-id") Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(value = "search-query", required = false) String searchQuery
    ) {
        limit = Math.max(1, Math.min(limit, 50)); // 한 페이지의 게시글 수를 1~50 사이로 제한
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending()); // 페이징 처리 (날짜순으로 정렬)

        return postService.postList(pageable, boardId, searchQuery);
    }

    @GetMapping("/posts/my") // 내 게시글 목록
    public ResponseEntity<PostMyListDto> myPostList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        limit = Math.max(1, Math.min(limit, 50)); // 한 페이지의 게시글 수를 1~50 사이로 제한
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending()); // 페이징 처리 (날짜순으로 정렬)

        return postService.myPostList(pageable);
    }

    @GetMapping("/posts/favorites") // 관심 자격증 게시글 목록
    public ResponseEntity<PostListDto> favoritePostList(
            @RequestParam(value = "board-id") Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        limit = Math.max(1, Math.min(limit, 50)); // 한 페이지의 게시글 수를 1~50 사이로 제한
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdDate").descending()); // 페이징 처리 (날짜순으로 정렬)

        return postService.favoritePostList(pageable, boardId);
    }
}
