package com.credential.cubrism.server.posts.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.favorites.repository.FavoriteRepository;
import com.credential.cubrism.server.posts.dto.*;
import com.credential.cubrism.server.posts.entity.*;
import com.credential.cubrism.server.posts.repository.BoardRepository;
import com.credential.cubrism.server.posts.repository.PostAiCommentsRepository;
import com.credential.cubrism.server.posts.repository.PostImagesRepository;
import com.credential.cubrism.server.posts.repository.PostRepository;
import com.credential.cubrism.server.qualification.entity.QualificationList;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import com.credential.cubrism.server.s3.utils.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final PostImagesRepository postImagesRepository;
    private final QualificationListRepository qualificationListRepository;
    private final FavoriteRepository favoriteRepository;
    private final AiResponseService aiResponseService;
    private final PostAiCommentsRepository postAiCommentsRepository;

    private final SecurityUtil securityUtil;
    private final S3Util s3util;


    // 게시글 작성
    @Transactional
    public ResponseEntity<MessageDto> addPost(PostAddDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        // 게시판
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        
        // 카테고리
        QualificationList qualificationList = qualificationListRepository.findByName(dto.getCategory())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Posts post = new Posts();
        post.setBoard(board);
        post.setUser(currentUser);
        post.setQualificationList(qualificationList);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        // 이미지 목록 저장
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

        aiResponseService.updatePostWithAiResponse(post);

        System.out.println("게시글 작성 완료");
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDto("게시글을 작성했습니다."));
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

        // 게시글 이미지 삭제
        post.getPostImages().forEach(postImage -> s3util.deleteFile(postImage.getImageUrl()));

        postRepository.delete(post);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("게시글을 삭제했습니다."));
    }

    // 게시글 수정
    @Transactional
    public ResponseEntity<MessageDto> updatePost(Long postId, PostUpdateDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        // 게시글
        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 카테고리
        QualificationList qualificationList = qualificationListRepository.findByName(dto.getCategory())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 작성자 본인인지 확인
        if (!post.getUser().getUuid().equals(currentUser.getUuid())) {
            throw new CustomException(ErrorCode.UPDATE_DENIED);
        }

        // 삭제할 이미지가 존재하면 S3 및 DB에서 이미지 삭제
        if (!dto.getRemovedImages().isEmpty()) {
            dto.getRemovedImages().forEach(imageUrl -> {
                s3util.deleteFile(imageUrl);
                postImagesRepository.deleteById(imageUrl);
            });
        }

        // 추가할 이미지가 존재하면 DB에 추가
        if (!dto.getImages().isEmpty()) {
            int maxIndex = post.getPostImages().stream()
                    .mapToInt(PostImages::getImageIndex)
                    .max()
                    .orElse(-1);

            List<PostImages> postImagesList = dto.getImages().stream()
                    .map(imageUrl -> {
                        PostImages postImage = new PostImages();
                        postImage.setPost(post);
                        postImage.setImageUrl(imageUrl);
                        postImage.setImageIndex(dto.getImages().indexOf(imageUrl) + maxIndex + 1);
                        return postImage;
                    }).collect(Collectors.toCollection(ArrayList::new));
            post.setPostImages(postImagesList);
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setQualificationList(qualificationList);
        if (post.getPostAiComments() != null) {
            postAiCommentsRepository.delete(post.getPostAiComments());
            post.setPostAiComments(null);
        }

        postRepository.save(post);

        aiResponseService.updatePostWithAiResponse(post);

        System.out.println("게시글 수정 완료");
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDto("게시글을 수정했습니다."));
    }

    // 게시글 목록
    public ResponseEntity<PostListDto> postList(Pageable pageable, Long boardId, String searchQuery) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // 검색어가 존재하면
        if (searchQuery != null) {
            searchQuery = searchQuery.toLowerCase().replace(" ", "");

            Page<Posts> posts = postRepository.findAllByBoardAndSearchQuery(board, searchQuery, pageable);

            return ResponseEntity.status(HttpStatus.OK).body(getPostList(posts));
        }

        // 검색어가 존재하지 않으면
        Page<Posts> posts = postRepository.findAllByBoard(board, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(getPostList(posts));
    }

    // 내 게시글 목록
    public ResponseEntity<PostMyListDto> myPostList(Pageable pageable) {
        Users currentUser = securityUtil.getCurrentUser();
        Page<Posts> posts = postRepository.findAllByUserUuid(currentUser.getUuid(), pageable);

        PostMyListDto.Pageable page = new PostMyListDto.Pageable(
                posts.hasPrevious() ? posts.getNumber() - 1 : null,
                posts.getNumber(),
                posts.hasNext() ? posts.getNumber() + 1 : null
        );

        List<PostMyListDto.PostList> postList = posts.stream()
                .map(post -> new PostMyListDto.PostList(
                        post.getPostId(),
                        post.getQualificationList().getName(),
                        post.getUser().getNickname(),
                        post.getUser().getImageUrl(),
                        post.getPostImages().stream()
                                .sorted(Comparator.comparingInt(PostImages::getImageIndex))
                                .map(PostImages::getImageUrl)
                                .toList(),
                        post.getTitle(),
                        post.getContent(),
                        getTimeAgoMyPost(post.getCreatedDate()),
                        (long) post.getComments().size()
                )).toList();

        return ResponseEntity.status(HttpStatus.OK).body(new PostMyListDto(page, postList));
    }

    // 관심 자격증 게시글 목록
    public ResponseEntity<PostListDto> favoritePostList(Pageable pageable, Long boardId) {
        Users currentUser = securityUtil.getCurrentUser();

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        List<String> favoriteCodes = favoriteRepository.findAllByUserUuid(currentUser.getUuid()).stream()
                .map(favorite -> favorite.getQualificationList().getCode())
                .toList();

        Page<Posts> posts = postRepository.findAllByBoardAndQualificationListCodeIn(board, favoriteCodes, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(getPostList(posts));
    }

    // 게시글 목록 불러오기
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
                        Optional.ofNullable(post.getUser())
                                .map(Users::getNickname)
                                .orElse(null),
                        post.getPostImages().stream()
                                .min(Comparator.comparingInt(PostImages::getImageIndex))
                                .map(PostImages::getImageUrl)
                                .orElse(null),
                        post.getTitle(),
                        post.getContent(),
                        getTimeAgo(post.getCreatedDate()),
                        (long) post.getComments().size()
                )).toList();

        return new PostListDto(pageableDTO, postListDTO);
    }

    // 게시글 보기
    public ResponseEntity<PostViewDto> postView(Long postId) {
        Posts post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        List<String> postImagesDto = postImagesRepository.findByPost(post, Sort.by(Sort.Direction.ASC, "imageIndex")).stream()
                .map(PostImages::getImageUrl)
                .toList();

        Map<Long, Comments> commentMap = post.getComments().stream()
                .collect(Collectors.toMap(Comments::getCommentId, Function.identity()));

        List<PostViewDto.Comments> commentsDto = post.getComments().stream()
                .map(comment -> {
                    Optional<Comments> replyToComment = Optional.ofNullable(comment.getReplyTo()).map(commentMap::get);
                    return new PostViewDto.Comments(
                            comment.getCommentId(),
                            comment.getReplyTo(),
                            replyToComment
                                    .map(Comments::getUser)
                                    .map(Users::getNickname)
                                    .orElse(null),
                            Optional.ofNullable(comment.getUser())
                                    .map(Users::getNickname)
                                    .orElse(null),
                            Optional.ofNullable(comment.getUser())
                                    .map(Users::getEmail)
                                    .orElse(null),
                            comment.getContent(),
                            comment.getCreatedDate().toString(),
                            Optional.ofNullable(comment.getUser())
                                    .map(Users::getImageUrl)
                                    .orElse(null),
                            comment.getModifiedDate() != null && comment.getModifiedDate().isAfter(comment.getCreatedDate())
                    );
                })
                .sorted(Comparator.comparing(PostViewDto.Comments::getCreatedDate))
                .toList();

        PostAiComments postAiComments = post.getPostAiComments();
        String aiResponse = postAiComments != null ? postAiComments.getContent() : null;

        PostViewDto dto = new PostViewDto(
                post.getPostId(),
                post.getBoard().getBoardName(),
                post.getQualificationList().getName(),
                Optional.ofNullable(post.getUser())
                        .map(Users::getNickname)
                        .orElse(null),
                Optional.ofNullable(post.getUser())
                        .map(Users::getImageUrl)
                        .orElse(null),
                Optional.ofNullable(post.getUser())
                        .map(Users::getEmail)
                        .orElse(null),
                post.getTitle(),
                post.getContent(),
                post.getCreatedDate().toString(),
                post.getModifiedDate().toString(),
                postImagesDto,
                commentsDto, aiResponse
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

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("M/d", Locale.getDefault());

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

    private String getTimeAgoMyPost(LocalDateTime date) {
        boolean isThisYear = LocalDateTime.now().getYear() == date.getYear();

        DateTimeFormatter dateFormat = isThisYear ?
                DateTimeFormatter.ofPattern("M.d HH:mm", Locale.getDefault()) :
                DateTimeFormatter.ofPattern("yyyy.M.d HH:mm", Locale.getDefault());

        return date.format(dateFormat);
    }
}
