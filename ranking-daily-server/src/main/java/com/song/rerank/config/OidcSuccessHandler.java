package com.song.rerank.config;

import com.song.rerank.security.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Component
@AllArgsConstructor
public class OidcSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String subject = oidcUser.getSubject();
        String name = oidcUser.getFullName();

        String token = tokenProvider.createToken(subject, name);


        // 前端整合方式 1：直接 JSON 回傳
        // response.setContentType("application/json");
        // response.getWriter().write("{\"token\":\"" + token + "\"}");


        // 前端整合方式 2：302 導回 SPA，token 夾在 URL fragment（避免落在 server log）
        String redirectUrl = "/post-login#token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
    }
}