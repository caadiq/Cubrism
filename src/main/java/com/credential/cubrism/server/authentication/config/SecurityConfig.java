package com.credential.cubrism.server.authentication.config;

import com.credential.cubrism.server.authentication.jwt.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // HTTP 기본 인증 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable)

                // CSRF 사용하지 않음
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 사용하지 않음
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 예외 처리
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패
                        .accessDeniedHandler(jwtAccessDeniedHandler)) // 권한 없음

                // 권한 설정
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/auth/signup/**").permitAll()
                        .requestMatchers("/auth/signin/**").permitAll()
                        .requestMatchers("/auth/reissue-access-token").permitAll()
                        .requestMatchers("/auth/social/**").permitAll()
                        .requestMatchers("/auth/password/find").permitAll()
                        .requestMatchers("/auth/password/reset/**").permitAll()
                        .requestMatchers("/qualification/**").permitAll()
                        .requestMatchers("/post/list").permitAll()
                        .requestMatchers("/post/view").permitAll()
                        .requestMatchers("/studygroup/list").permitAll()
                        .requestMatchers("/api/fcm").permitAll()
                        .requestMatchers("/test/**").permitAll()
                        .anyRequest().authenticated()
                )

                // JWT 인증 필터
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                // JWT 예외 처리 필터
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}