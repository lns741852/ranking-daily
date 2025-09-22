package com.song.rerank.config;

import com.song.rerank.properties.SecurityProperties;
import com.song.rerank.security.JwtAccessDeniedHandler;
import com.song.rerank.security.JwtAuthenticationEntryPoint;
import com.song.rerank.security.TokenFilter;
import com.song.rerank.security.TokenProvider;
import com.song.rerank.service.UserCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import static com.song.rerank.utils.EnumUtils.getAnonymousPaths;


/**
 * scurity 配置
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint authenticationErrorHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final SecurityProperties properties;
    private final UserCacheService userCacheService;
    private final  OidcSuccessHandler oidcSuccessHandler;


    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults(""); // 去除 ROLE_ 前缀
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //加密
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //禁止CSRF
                .csrf(AbstractHttpConfigurer::disable)
                /**
                 *  filter
                 *  1. Before UsernamePasswordAuthenticationFilter 執行登入前的檢查
                 *  2. Before BasicAuthenticationFilter 執行登入檢查
                 *  3. Before SecurityContextPersistenceFilter 紀錄請求
                 *  4. Before FilterSecurityInterceptor 其他邏輯
                 */
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                // 防止 iframe 跨域
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(getAnonymousPaths()).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyRequest().authenticated()
                ).oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/google") // 可直接用此 endpoint 觸發登入
                        .successHandler(oidcSuccessHandler)
                )
                .addFilterBefore(new TokenFilter(tokenProvider, properties),
                        BasicAuthenticationFilter.class)
                // 登入異常處理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationErrorHandler)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                );
        ;


        return http.build();
    }
}
