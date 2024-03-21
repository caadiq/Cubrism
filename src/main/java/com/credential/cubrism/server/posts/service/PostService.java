package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.posts.dto.*;
import com.credential.cubrism.server.posts.entity.Board;
import com.credential.cubrism.server.posts.entity.PostImages;
import com.credential.cubrism.server.posts.entity.Posts;
import com.credential.cubrism.server.posts.repository.BoardRepository;
import com.credential.cubrism.server.posts.repository.PostImagesRepository;
import com.credential.cubrism.server.posts.repository.PostRepository;
import com.credential.cubrism.server.qualification.entity.QualificationList;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import com.credential.cubrism.server.s3.utils.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PostService {
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final PostImagesRepository postImagesRepository;
    private final QualificationListRepository qualificationListRepository;

    private final SecurityUtil securityUtil;
    private final S3Util s3util;

    // 게시글 작성
    @Transactional
    public ResponseEntity<MessageDto> addPost(PostAddDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        Board board = boardRepository.findByBoardName(dto.getBoardName())
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        QualificationList qualificationList = qualificationListRepository.findByName(dto.getCategory())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Posts post = new Posts();
        post.setBoard(board);
        post.setUser(currentUser);
        post.setQualificationList(qualificationList);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        List<PostImages> postImagesList = dto.getImages().stream()
                .map(imageUrl -> {
                    PostImages postImage = new PostImages();
                    postImage.setPost(post);
                    postImage.setImageUrl(imageUrl);
                    postImage.setImageIndex(dto.getImages().indexOf(imageUrl));
                    return postImage;
                }).toList();

        post.setPostImages(postImagesList);
        postRepository.save(post);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("게시글을 작성했습니다."));
    }

    // 게시글 삭제
    @Transactional
    public ResponseEntity<MessageDto> deletePost(PostDeleteDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        Posts post = postRepository.findByPostIdAndBoardName(dto.getPostId(), dto.getBoardName())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 작성자 본인인지 확인
        if (!post.getUser().getUuid().equals(currentUser.getUuid())) {
            throw new CustomException(ErrorCode.DELETE_DENIED);
        }

        postRepository.delete(post);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("게시글을 삭제했습니다."));
    }

    // 게시글 수정
    @Transactional
    public ResponseEntity<MessageDto> updatePost(PostUpdateDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        Posts post = postRepository.findByPostId(dto.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 작성자 본인인지 확인
        if (!post.getUser().getUuid().equals(currentUser.getUuid())) {
            throw new CustomException(ErrorCode.UPDATE_DENIED);
        }

        // 게시판과 게시글이 일치하는지 확인
        if (!post.getBoard().getBoardName().equals(dto.getBoardName())) {
            throw new CustomException(ErrorCode.INVALID_BOARD_AND_POST);
        }

        // RemovedImages가 존재하면 S3및 DB에서 이미지 삭제
        if (dto.getRemovedImages() != null) {
            for (String removedImageUrl : dto.getRemovedImages()) {
                s3util.deleteFile(removedImageUrl);
                postImagesRepository.deleteByImageUrl(removedImageUrl);
            }
        }

        List<PostImages> postImagesList = dto.getImages().stream()
                .map(imageUrl -> {
                    PostImages postImage = new PostImages();
                    postImage.setPost(post);
                    postImage.setImageUrl(imageUrl);
                    postImage.setImageIndex(dto.getImages().indexOf(imageUrl));
                    return postImage;
                }).collect(Collectors.toCollection(ArrayList::new));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setPostImages(postImagesList);

        postRepository.save(post);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("게시글을 수정했습니다."));
    }

    // 게시글 목록
    public ResponseEntity<PostListDto> postList(Pageable pageable, String boardName) {
        Board board = boardRepository.findByBoardName(boardName)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        Page<Posts> posts = postRepository.findAllByBoard(board, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(getPostList(posts));
    }

    // 내 게시글 목록
    public ResponseEntity<PostListDto> myPostList(Pageable pageable) {
        Users currentUser = securityUtil.getCurrentUser();

        Page<Posts> posts = postRepository.findAllByUserUuid(currentUser.getUuid(), pageable);

        return ResponseEntity.status(HttpStatus.OK).body(getPostList(posts));
    }

    private PostListDto getPostList(Page<Posts> posts) {
        PostListDto.Pageable pageableDTO = new PostListDto.Pageable(
                posts.hasPrevious() ? posts.getNumber() - 1 : null,
                posts.getNumber(),
                posts.hasNext() ? posts.getNumber() + 1 : null
        );

        List<PostListDto.PostList> postListDTO = posts.stream()
                .map(post -> new PostListDto.PostList(
                        post.getPostId(),
                        post.getBoard().getBoardName(),
                        post.getQualificationList().getName(),
                        post.getUser().getNickname(),
                        post.getPostImages().stream()
                                .filter(image -> image.getImageIndex() == 0)
                                .findFirst()
                                .map(PostImages::getImageUrl)
                                .orElse(null),
                        post.getTitle(),
                        post.getContent(),
                        post.getCreatedDate().toString(),
                        post.getComments().stream()
                                .flatMap(comment -> Stream.concat(Stream.of(comment), comment.getReplies().stream()))
                                .count()
                )).toList();

        return new PostListDto(pageableDTO, postListDTO);
    }

    // 게시글 보기
    public ResponseEntity<PostViewDto> postView(Long postId, String boardName) {
        Posts post = postRepository.findByPostIdAndBoardName(postId, boardName)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<String> postImagesDto = postImagesRepository.findAllByPostId(postId).stream()
                .map(PostImages::getImageUrl)
                .toList();

        List<PostViewDto.Comments> commentsDto = post.getComments().stream()
                .flatMap(comment -> Stream.concat(
                        Stream.of(new PostViewDto.Comments(
                                comment.getCommentId(),
                                null,
                                comment.getUser().getNickname(),
                                comment.getUser().getEmail(),
                                comment.getContent(),
                                comment.getCreatedDate().toString(),
                                comment.getUser().getImageUrl(),
                                false
                        )),
                        comment.getReplies().stream()
                                .map(reply -> new PostViewDto.Comments(
                                        reply.getReplyId(),
                                        comment.getCommentId(),
                                        reply.getUser().getNickname(),
                                        reply.getUser().getEmail(),
                                        reply.getContent(),
                                        reply.getCreatedDate().toString(),
                                        reply.getUser().getImageUrl(),
                                        true
                                ))
                ))
                .sorted(Comparator.comparing(PostViewDto.Comments::getCreatedDate))
                .toList();

        PostViewDto dto = new PostViewDto(
                post.getPostId(),
                post.getBoard().getBoardName(),
                post.getUser().getNickname(),
                post.getUser().getEmail(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedDate().toString(),
                post.getModifiedDate().toString(),
                postImagesDto,
                commentsDto
        );

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    // 카테고리 목록
    @Cacheable("categoryList")
    public List<CategoryListDto> categoryList() {
        return qualificationListRepository.findAll().stream()
                .map(qualificationList -> new CategoryListDto(
                        qualificationList.getCode(),
                        qualificationList.getName()
                )).toList();
    }
}
