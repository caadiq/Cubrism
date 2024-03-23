package com.credential.cubrism.server.authentication.dto;

import lombok.Getter;

@Getter
public class KakaoUserDto {
    private KakaoAccount kakao_account;

    @Getter
    public static class KakaoAccount {
        private Profile profile;
        private String email;
    }

    @Getter
    public static class Profile {
        private String nickname;
        private String profile_image_url;
    }
}