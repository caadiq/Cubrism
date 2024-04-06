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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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

        Board board = boardRepository.findById(dto.getBoardId())
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
    public ResponseEntity<MessageDto> deletePost(Long postId) {
        Users currentUser = securityUtil.getCurrentUser();

        Posts post = postRepository.findById(postId)
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
    public ResponseEntity<MessageDto> updatePost(Long postId, PostUpdateDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 작성자 본인인지 확인
        if (!post.getUser().getUuid().equals(currentUser.getUuid())) {
            throw new CustomException(ErrorCode.UPDATE_DENIED);
        }

        // RemovedImages가 존재하면 S3및 DB에서 이미지 삭제
        if (dto.getRemovedImages() != null) {
            for (String removedImageUrl : dto.getRemovedImages()) {
                s3util.deleteFile(removedImageUrl);
                postImagesRepository.deleteByImageUrl(removedImageUrl);
            }
        }

        // 이미지 목록을 업데이트
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
    public ResponseEntity<PostListDto> postList(Pageable pageable, Long boardId, String searchQuery) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (searchQuery != null) {
            searchQuery = searchQuery.toLowerCase().replace(" ", "");

            Page<Posts> posts = postRepository.findAllByBoardAndSearchQuery(board, searchQuery, pageable);
            return ResponseEntity.status(HttpStatus.OK).body(getPostList(posts));
        }

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
                        post.getQualificationList().getName(),
                        post.getUser().getNickname(),
                        post.getPostImages().stream()
                                .filter(image -> image.getImageIndex() == 0)
                                .findFirst()
                                .map(PostImages::getImageUrl)
                                .orElse(null),
                        post.getTitle(),
                        post.getContent(),
                        getTimeAgo(post.getCreatedDate()),
                        post.getComments().stream()
                                .flatMap(comment -> Stream.concat(Stream.of(comment), comment.getReplies().stream()))
                                .count()
                )).toList();

        return new PostListDto(pageableDTO, postListDTO);
    }

    // 게시글 보기
    public ResponseEntity<PostViewDto> postView(Long postId) {
        Posts post = postRepository.findById(postId)
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
                post.getQualificationList().getName(),
                post.getUser().getNickname(),
                post.getUser().getImageUrl(),
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

    private String getTimeAgo(LocalDateTime date) {
        // 현재 날짜와 게시글 날짜의 차이 계산
        Duration diff = Duration.between(date, LocalDateTime.now());

        // 시간 단위로 변환
        long seconds = diff.getSeconds();
        long minutes = diff.toMinutes();
        long hours = diff.toHours();
        long days = diff.toDays();

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("M/dd", Locale.getDefault());

        if (seconds < 60) { // 60초 이내
            return "방금 전";
        } else if (minutes < 60) { // 60분 이내
            return minutes + "분 전";
        } else if (hours < 24) { // 하루 이내
            return hours + "시간 전";
        } else if (days < 7) { // 1주일 이내
            return days + "일 전";
        } else { // 그 외
            return date.format(dateFormat);
        }
    }
}
