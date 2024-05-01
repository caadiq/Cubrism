package com.credential.cubrism.server.favorites.service;

import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.utils.SecurityUtil;
import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.common.exception.CustomException;
import com.credential.cubrism.server.common.exception.ErrorCode;
import com.credential.cubrism.server.favorites.dto.FavoriteAddDto;
import com.credential.cubrism.server.favorites.dto.FavoriteListDto;
import com.credential.cubrism.server.favorites.entity.Favorites;
import com.credential.cubrism.server.favorites.repository.FavoriteRepository;
import com.credential.cubrism.server.qualification.entity.QualificationList;
import com.credential.cubrism.server.qualification.repository.QualificationListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final QualificationListRepository qualificationListRepository;

    private final SecurityUtil securityUtil;

    // 관심 자격증 추가
    @Transactional
    public ResponseEntity<MessageDto> addFavorite(FavoriteAddDto dto) {
        Users currentUser = securityUtil.getCurrentUser();

        // 자격증
        QualificationList qualificationList = qualificationListRepository.findById(dto.getCode())
                .orElseThrow(() -> new CustomException(ErrorCode.QUALIFICATION_NOT_FOUND));

        // 이미 추가된 자격증인지 확인
        if (favoriteRepository.existsByUserAndQualificationList(currentUser, qualificationList)) {
            throw new CustomException(ErrorCode.FAVORITE_ALREADY_ADDED);
        }

        Favorites favorite = new Favorites();
        favorite.setUser(currentUser);
        favorite.setQualificationList(qualificationList);
        favoriteRepository.save(favorite);

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDto("관심 자격증이 추가되었습니다."));
    }

    // 관심 자격증 삭제
    @Transactional
    public ResponseEntity<MessageDto> deleteFavorite(Long favoriteId) {
        Users currentUser = securityUtil.getCurrentUser();

        Favorites favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new CustomException(ErrorCode.FAVORITE_NOT_FOUND));

        // 작성자 본인인지 확인
        if (!favorite.getUser().getUuid().equals(currentUser.getUuid())) {
            throw new CustomException(ErrorCode.DELETE_DENIED);
        }

        favoriteRepository.delete(favorite);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto("관심 자격증이 삭제되었습니다."));
    }

    // 관심 자격증 목록
    public ResponseEntity<List<FavoriteListDto>> favoriteList() {
        Users currentUser = securityUtil.getCurrentUser();

        List<FavoriteListDto> favoriteList = favoriteRepository.findAllByUserUuid(currentUser.getUuid()).stream()
                .map(favorite -> new FavoriteListDto(
                        favorite.getFavoriteId(),
                        favorite.getQualificationList().getCode(),
                        favorite.getQualificationList().getName()
                ))
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(favoriteList);
    }
}
