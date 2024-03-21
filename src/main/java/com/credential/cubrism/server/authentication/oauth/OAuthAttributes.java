package com.credential.cubrism.server.authentication.oauth;

import java.util.Map;

public record OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if("kakao".equals(registrationId)) {
            return ofKakao(attributes);
        }

        return ofGoogle(userNameAttributeName, attributes);
    }

    // 구글
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return new OAuthAttributes(attributes, userNameAttributeName, (String) attributes.get("name"), (String) attributes.get("email"), (String) attributes.get("picture"));
    }

    // 카카오
    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return new OAuthAttributes(attributes, "id", (String) kakaoProfile.get("nickname"), (String) kakaoAccount.get("email"), (String) kakaoProfile.get("profile_image_url"));
    }
}