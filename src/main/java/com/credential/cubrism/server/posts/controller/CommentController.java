package com.credential.cubrism.server.posts.controller;

import com.credential.cubrism.server.posts.model.Comment;
import com.credential.cubrism.server.posts.model.Posts;
import com.credential.cubrism.server.posts.dto.CommentCreateRequest;
import com.credential.cubrism.server.posts.service.CommentService;
import com.credential.cubrism.server.posts.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@ResponseBody
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    @PostMapping("/comments/{boardId}")
    public String addComments(@PathVariable Long boardId, @RequestBody CommentCreateRequest req,
                              Authentication auth) {
        commentService.writeComment(boardId, req, auth.getName());

        return "Success";
    }

    @GetMapping("/{category}/{postId}/comments")
    @ResponseBody
    public Object returnComments(@PathVariable String category, @PathVariable Long postId, Authentication auth) {
        Posts post = postService.getPostByPostId(postId);
        List<Comment> comments = commentService.findAll(postId);

        if (post == null) {
            return "Post not found";
        }

        if (!post.getCategory().equals(category)) {
            return "category not match";
        }

        return comments;
    }

}
