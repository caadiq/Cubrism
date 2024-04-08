package com.credential.cubrism.server.favorites.controller;

import com.credential.cubrism.server.common.dto.MessageDto;
import com.credential.cubrism.server.favorites.dto.FavoriteAddDto;
import com.credential.cubrism.server.favorites.dto.FavoriteListDto;
import com.credential.cubrism.server.favorites.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    // 관심 자격증 추가
    @PostMapping("/favorite")
    public ResponseEntity<MessageDto> addFavorite(@RequestBody FavoriteAddDto dto) {
        return favoriteService.addFavorite(dto);
    }

    // 관심 자격증 삭제
    @DeleteMapping("/favorite/{favoriteId}")
    public ResponseEntity<MessageDto> deleteFavorite(@PathVariable Long favoriteId) {
        return favoriteService.deleteFavorite(favoriteId);
    }

    // 관심 자격증 목록
    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteListDto>> favoriteList() {
        return favoriteService.favoriteList();
    }
}
