package com.song.rerank.config;

import com.song.rerank.annotation.AnonymousAccess;
import com.song.rerank.config.properties.SecurityProperties;
import com.song.rerank.enums.RequestMethodEnum;
import com.song.rerank.security.JwtAccessDeniedHandler;
import com.song.rerank.security.JwtAuthenticationEntryPoint;
import com.song.rerank.security.TokenFilter;
import com.song.rerank.security.TokenProvider;
import com.song.rerank.service.UserCacheService;
import com.song.rerank.service.UserStatuService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint authenticationErrorHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final ApplicationContext applicationContext;
    private final SecurityProperties properties;
    private final UserStatuService userStatuService;
    private final UserCacheService userCacheService;
    private final AuthenticationSuccessHandler oidcSuccessHandler;


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
        // 取得 @AnonymousAccess 的匿名登入可用url
        RequestMappingHandlerMapping mapping = (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping");
        Map<RequestMappingInfo, HandlerMethod> methodMap = mapping.getHandlerMethods();
        Map<String, Set<String>> anonymousUrls = getAnonymousUrl(methodMap);

        http
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //禁止CSRF
                .csrf(AbstractHttpConfigurer::disable)
                //filter
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new TokenFilter(tokenProvider, properties, userStatuService, userCacheService),
                        UsernamePasswordAuthenticationFilter.class)
                // 登入異常處理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationErrorHandler)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                // 防止 iframe 跨域
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 靜態資源、swagger、druid
                        .requestMatchers(
                                HttpMethod.GET,
                                "/*.html", "/**.html", "/**.css", "/**.js", "/webSocket/**"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/*/api-docs",
                                "/avatar/**", "/file/**", "/druid/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 加入匿名訪問URL
                        .requestMatchers(HttpMethod.GET, anonymousUrls.get("GET").toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.POST, anonymousUrls.get("POST").toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.PUT, anonymousUrls.get("PUT").toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.PATCH, anonymousUrls.get("PATCH").toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.DELETE, anonymousUrls.get("DELETE").toArray(new String[0])).permitAll()

                        // 其他所有請求都需驗證
                        .anyRequest().authenticated()

                ).oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/google") // 可直接用此 endpoint 觸發登入
                        .successHandler(oidcSuccessHandler)
                )
                // 若你的 API 要用你自己簽發的 JWT 來保護，也可啟用 Resource Server：
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                ).exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\":\"UNAUTHORIZED\"}");
                        })
                );;


        return http.build();
    }

    private Map<String, Set<String>> getAnonymousUrl(Map<RequestMappingInfo, HandlerMethod> handlerMethodMap) {
        Map<String, Set<String>> anonymousUrls = new HashMap<>();
        for (RequestMethodEnum method : RequestMethodEnum.values()) {
            anonymousUrls.put(method.getType(), new HashSet<>());
        }

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            AnonymousAccess anonymousAccess = handlerMethod.getMethodAnnotation(AnonymousAccess.class);
            if (anonymousAccess != null) {
                List<RequestMethod> requestMethods = new ArrayList<>(entry.getKey().getMethodsCondition().getMethods());
                RequestMethodEnum methodEnum = RequestMethodEnum.find(
                        requestMethods.isEmpty() ? "ALL" : requestMethods.get(0).name()
                );
                anonymousUrls.get(methodEnum.getType()).addAll(entry.getKey().getPathPatternsCondition().getPatternValues());
            }
        }
        return anonymousUrls;
    }
}
