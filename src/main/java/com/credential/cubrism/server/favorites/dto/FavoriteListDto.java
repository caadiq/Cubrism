package com.credential.cubrism.server.favorites.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteListDto {
    private Integer index;
    private Long favoriteId;
    private String code;
    private String name;
}
