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


/**
 * google oauth2
 */
@Component
@AllArgsConstructor
public class OidcSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String subject = oidcUser.getSubject();
        String account = oidcUser.getFullName();
        String token = tokenProvider.createToken(account);




        String redirectUrl = "http://localhost:5173/#/login?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
    }
}