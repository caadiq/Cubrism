package com.credential.cubrism.server.authentication.config;

import com.credential.cubrism.server.authentication.jwt.CustomAuthenticationEntryPoint;
import com.credential.cubrism.server.authentication.jwt.JwtTokenFilter;
import com.credential.cubrism.server.authentication.service.PrincipalOauth2UserService;
import com.credential.cubrism.server.authentication.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthService authService;
    private final PrincipalOauth2UserService principalOauth2UserService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.authenticationEntryPoint(new CustomAuthenticationEntryPoint())) // 인증되지 않은 사용자가 리소스에 액세스 할 때 호출되는 커스텀 AuthenticationEntryPoint(나중에 지워도 됨)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer -> { // 세션 사용 안함(jwt 사용시 세션 사용 안함)
                    sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/auth/signup/**").permitAll()
                        .requestMatchers("/auth/signin/**").permitAll()
                        .requestMatchers("/auth/refresh/**").permitAll()
                        .requestMatchers("/oauth2/authorization/google").permitAll()
                        .requestMatchers("/qualification/**").permitAll()
                        .requestMatchers("/auth/googleLoginTest/**").permitAll()
                        .requestMatchers("/post/list").permitAll()
                        .requestMatchers("/post/view").permitAll()
                        .requestMatchers("/test/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login -> oauth2Login.defaultSuccessUrl("/googleLoginTest", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(principalOauth2UserService)
                        )
                )
                .logout(logout -> logout.logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies("JSESSIONID"))
                .addFilterBefore(new JwtTokenFilter(authService, secretKey), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}