package com.credential.cubrism.server.authentication.config;

import com.credential.cubrism.server.Jwt.JwtTokenFilter;
import com.credential.cubrism.server.Jwt.PrincipalOauth2UserService;
import com.credential.cubrism.server.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserService userService;
    private final PrincipalOauth2UserService principalOauth2UserService;
    private static String secretKey = "my-secret-key-123123";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer -> { // 세션 사용 안함(jwt 사용시 세션 사용 안함)
                    sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authorizeRequests((authz) -> authz
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/auth/signup/**").permitAll()
                        .requestMatchers("/jwt-login/login").permitAll()
                        .requestMatchers("/googleLoginTest").permitAll()
                        .requestMatchers("/oauth2/authorization/google").permitAll()
                        .requestMatchers("/home").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login -> {
                    oauth2Login.defaultSuccessUrl("/googleLoginTest", true)
                            .userInfoEndpoint()
                            .userService(principalOauth2UserService);
                })
                .logout(logout -> {
                    logout.logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies("JSESSIONID");
                })
                .addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}