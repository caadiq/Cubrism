package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.authentication.utils.AuthenticationUtil;
import com.credential.cubrism.server.common.utils.S3ImageUploadUtil;
import com.credential.cubrism.server.posts.dto.PostListGetDTO;
import com.credential.cubrism.server.posts.dto.PostRegisterPostDTO;
import com.credential.cubrism.server.posts.dto.PostUpdatePostDTO;
import com.credential.cubrism.server.posts.model.Board;
import com.credential.cubrism.server.posts.model.PostImages;
import com.credential.cubrism.server.posts.model.Posts;
import com.credential.cubrism.server.posts.repository.BoardRepository;
import com.credential.cubrism.server.posts.repository.PostImagesRepository;
import com.credential.cubrism.server.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final S3ImageUploadUtil s3ImageUploadUtil;
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImagesRepository postImagesRepository;

    private final static String filePath = "post_images/";

    @Transactional
    public void registerPost(List<MultipartFile> files, PostRegisterPostDTO dto, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);

        Board board = boardRepository.findByBoardName(dto.getBoardName())
                .orElseThrow(() -> new IllegalArgumentException("Board not found with name : " + dto.getBoardName()));

        Posts post = new Posts();
        post.setBoard(board);
        post.setUser(user);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        // 이미지 업로드 후 이미지 URL 받아와서 리스트로 저장
        List<String> imageUrls;
        try {
            imageUrls = s3ImageUploadUtil.uploadImages("post", files, 5, filePath, user.getUuid());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        postRepository.save(post); // 이미지가 정상적으로 업로드가 되면 게시글 저장

        // 이미지 URL을 PostImages에 저장
        List<PostImages> postImageList = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            PostImages postImages = new PostImages();
            postImages.setPost(post);
            postImages.setImageUrl(imageUrl);
            postImages.setImageIndex(imageUrls.indexOf(imageUrl));
            postImageList.add(postImages);
        }

        postImagesRepository.saveAll(postImageList);
    }

    @Transactional
    public void updatePost(PostUpdatePostDTO dto, Authentication authentication) {
        Users user = AuthenticationUtil.getUserFromAuthentication(authentication, userRepository);

        Board board = boardRepository.findByBoardName(dto.getBoardName())
                .orElseThrow(() -> new IllegalArgumentException("Board not found with name : " + dto.getBoardName()));

        Posts post = postRepository.findByUserIdAndPostId(user.getUuid(), dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id : " + dto.getPostId()));

        post.setBoard(board);
        post.setUser(user);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        postRepository.save(post);
    }

    public PostListGetDTO postList(Pageable pageable, String boardName) {
        Board board = boardRepository.findByBoardName(boardName)
                .orElseThrow(() -> new IllegalArgumentException("Board not found with name : " + boardName));
        Page<Posts> posts = postRepository.findAllByBoard(board, pageable);

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
                        post.getCreatedDate().toString()
                )).collect(Collectors.toList());

        return new PostListGetDTO(pageableDTO, postListDTO);
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
