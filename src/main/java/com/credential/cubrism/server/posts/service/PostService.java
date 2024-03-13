package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.authentication.utils.AuthenticationUtil;
import com.credential.cubrism.server.posts.dto.*;
import com.credential.cubrism.server.posts.model.Board;
import com.credential.cubrism.server.posts.model.PostImages;
import com.credential.cubrism.server.posts.model.Posts;
import com.credential.cubrism.server.posts.repository.BoardRepository;
import com.credential.cubrism.server.posts.repository.PostImagesRepository;
import com.credential.cubrism.server.posts.repository.PostRepository;
import com.credential.cubrism.server.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImagesRepository postImagesRepository;
    private final S3Service s3Service;

    @Transactional
    public void addPost(PostAddPostDTO dto, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);

        Board board = boardRepository.findByBoardName(dto.getBoardName())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다"));

        Posts post = new Posts();
        post.setBoard(board);
        post.setUser(user);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        List<PostImages> postImagesList = new ArrayList<>();
        for (PostAddPostDTO.Images image : dto.getImages()) {
            PostImages postImage = new PostImages();
            postImage.setPost(post);
            postImage.setImageUrl(image.getImageUrl());
            postImage.setImageIndex(dto.getImages().indexOf(image));
            postImagesList.add(postImage);
        }
        post.setPostImages(postImagesList);
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(PostDeletePostDTO dto, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);

        Posts post = postRepository.findByPostIdAndBoardName(dto.getPostId(), dto.getBoardName())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));

        if (!post.getUser().getUuid().equals(user.getUuid())) {
            throw new IllegalArgumentException("본인만 삭제할 수 있습니다");
        }

        postRepository.delete(post);
    }

    @Transactional
    public void updatePost(PostUpdatePostDTO dto, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);

        Posts post = postRepository.findByPostId(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));

        if (!post.getUser().getUuid().equals(user.getUuid())) {
            throw new IllegalArgumentException("본인만 수정할 수 있습니다");
        }

        if (!post.getBoard().getBoardName().equals(dto.getBoardName())) {
            throw new IllegalArgumentException("게시판과 게시글이 일치하지 않습니다");
        }

        if (dto.getRemovedImages() != null) {
            for (PostUpdatePostDTO.RemovedImages removedImage : dto.getRemovedImages()) {
                s3Service.deleteFileFromS3(removedImage.getImageUrl());
                postImagesRepository.deleteByImageUrl(removedImage.getImageUrl());
            }
        }

        List<PostImages> postImagesList = new ArrayList<>();
        for (PostUpdatePostDTO.Images image : dto.getImages()) {
            PostImages postImage = new PostImages();
            postImage.setPost(post);
            postImage.setImageUrl(image.getImageUrl());
            postImage.setImageIndex(dto.getImages().indexOf(image));
            postImagesList.add(postImage);
        }
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setPostImages(postImagesList);

        postRepository.save(post);
    }

    public PostListGetDTO postList(Pageable pageable, String boardName) {
        Board board = boardRepository.findByBoardName(boardName)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시판이 존재하지 않습니다"));
        Page<Posts> posts = postRepository.findAllByBoard(board, pageable);

        return getPostList(posts);
    }

    public PostListGetDTO myPostList(Pageable pageable, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);
        Page<Posts> posts = postRepository.findAllByUserUuid(user.getUuid(), pageable);

        return getPostList(posts);
    }

    private PostListGetDTO getPostList(Page<Posts> posts) {
        PostListGetDTO.Pageable pageableDTO = new PostListGetDTO.Pageable(
                posts.hasPrevious() ? posts.getNumber() - 1 : null,
                posts.getNumber(),
                posts.hasNext() ? posts.getNumber() + 1 : null
        );

        List<PostListGetDTO.PostList> postListDTO = posts.stream().map(post ->
                new PostListGetDTO.PostList(
                        post.getPostId(),
                        post.getBoard().getBoardName(),
                        post.getUser().getNickname(),
                        post.getPostImages().stream()
                                .filter(image -> image.getImageIndex() == 0)
                                .findFirst()
                                .map(PostImages::getImageUrl)
                                .orElse(null),
                        post.getTitle(),
                        post.getContent(),
                        post.getCreatedDate().toString(),
                        post.getComments().size()
                )).collect(Collectors.toList());

        return new PostListGetDTO(pageableDTO, postListDTO);
    }

    public PostViewGetDTO postView(Long postId, String boardName) {
        Posts post = postRepository.findByPostIdAndBoardName(postId, boardName)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다"));

        List<PostImages> postImages = postImagesRepository.findAllByPostId(postId);
        List<PostViewGetDTO.Images> postImagesDTO = postImages.stream()
                .map(image -> new PostViewGetDTO.Images(image.getImageUrl()))
                .collect(Collectors.toList());
        List<PostViewGetDTO.Comments> commentsDTO = post.getComments().stream()
                .map(comment -> new PostViewGetDTO.Comments(
                        comment.getCommentId(),
                        comment.getUser().getNickname(),
                        comment.getUser().getEmail(),
                        comment.getUser().getImageUrl(),
                        comment.getContent(),
                        comment.getCreatedDate().toString()
                ))
                .collect(Collectors.toList());

        return new PostViewGetDTO(
                post.getPostId(),
                post.getBoard().getBoardName(),
                post.getUser().getNickname(),
                post.getUser().getEmail(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedDate().toString(),
                post.getModifiedDate().toString(),
                postImagesDTO,
                commentsDTO
        );
    }
}
