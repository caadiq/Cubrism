package com.credential.cubrism.server.authentication.service;

import com.credential.cubrism.server.authentication.model.Users;
import com.credential.cubrism.server.authentication.oauth.GoogleUserInfo;
import com.credential.cubrism.server.authentication.oauth.KakaoUserInfo;
import com.credential.cubrism.server.authentication.oauth.OAuth2UserInfo;
import com.credential.cubrism.server.authentication.oauth.PrincipalDetails;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = null;

        String provider = userRequest.getClientRegistration().getRegistrationId();

        if (provider.equals("google")) {
            log.info("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (provider.equals("kakao")) {
            log.info("카카오 로그인 요청");
            Map<String, Object> map = oAuth2User.getAttributes();
            oAuth2UserInfo = new KakaoUserInfo(map);
        }

        String providerId = Objects.requireNonNull(oAuth2UserInfo).getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String nickname = oAuth2UserInfo.getName();


        Optional<Users> optionalUser = userRepository.findByEmail(email);
        Users user;

        if (optionalUser.isEmpty()) {
            user = Users.builder()
                    .email(email)
                    .nickname(nickname)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(user);
        } else {
            user = optionalUser.get();
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}