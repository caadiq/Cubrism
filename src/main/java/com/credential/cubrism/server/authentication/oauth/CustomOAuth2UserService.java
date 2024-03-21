package com.credential.cubrism.server.authentication.oauth;

import com.credential.cubrism.server.authentication.entity.Authority;
import com.credential.cubrism.server.authentication.entity.Users;
import com.credential.cubrism.server.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = service.loadUser(userRequest);  // OAuth2 인증 서비스에서 유저 정보를 가져옴

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 유저 정보에서 필요한 정보 추출
        OAuthAttributes attributes = OAuthAttributes.of(
                registrationId,
                userNameAttributeName,
                oAuth2User.getAttributes());

        // 추출된 사용자 정보에서 필요한 데이터 추출
        String email = attributes.email();
        String nickname = attributes.name();
        String profileImageUrl = attributes.picture();

        Optional<Users> currentUser = userRepository.findByEmail(email);
        Users user;

        if (currentUser.isPresent()) { // 이메일로 이미 가입한 유저라면 저장된 유저 객체를 가져옴
            user = currentUser.get();
            user.setPassword(null); // 소셜 로그인은 비밀번호가 필요하지 않으므로 null로 설정
        } else { // 새로 가입하는 유저라면 새로운 유저 객체를 생성
            Set<Authority> authorities = new HashSet<>();
            authorities.add(new Authority("ROLE_USER"));

            user = new Users();
            user.setEmail(email);
            user.setAuthorities(authorities);
        }

        user.setNickname(nickname);
        user.setImageUrl(profileImageUrl);
        user.setProvider(registrationId);

        userRepository.save(user);

        return oAuth2User;
    }
}