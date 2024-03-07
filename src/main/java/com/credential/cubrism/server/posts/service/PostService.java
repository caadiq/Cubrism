package com.credential.cubrism.server.posts.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import com.credential.cubrism.server.authentication.utils.AuthenticationUtil;
import com.credential.cubrism.server.posts.dto.PostRegisterPostDTO;
import com.credential.cubrism.server.posts.dto.PostUpdatePostDTO;
import com.credential.cubrism.server.posts.model.Board;
import com.credential.cubrism.server.posts.model.PostImages;
import com.credential.cubrism.server.posts.model.Posts;
import com.credential.cubrism.server.posts.repository.BoardRepository;
import com.credential.cubrism.server.posts.repository.PostImagesRepository;
import com.credential.cubrism.server.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    private final AmazonS3 s3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
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
        postRepository.save(post);

        try {
            for (MultipartFile file : files) {
                String fileType = file.getContentType();

                String originalFileName = file.getOriginalFilename(); // 원본 파일명
                String extension = Objects.requireNonNull(originalFileName).substring(originalFileName.lastIndexOf(".")); // 파일 확장자

                String fileName = filePath + user.getUuid() + "_" + UUID.randomUUID() + extension; // 파일 이름

                ObjectMetadata metadata = new ObjectMetadata(); // 파일 메타데이터
                metadata.setContentLength(file.getSize()); // 파일 크기
                metadata.setContentType(fileType); // 파일 MIME 타입

                s3.putObject(bucketName, fileName, file.getInputStream(), metadata); // S3 버킷에 파일 업로드

                PostImages postImages = new PostImages();
                postImages.setPost(post);
                postImages.setImageUrl(s3.getUrl(bucketName, fileName).toString());
                postImagesRepository.save(postImages);
            }
        } catch (Exception e) {
            postRepository.delete(post);
            throw new IllegalArgumentException("이미지 업로드 실패");
        }
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
