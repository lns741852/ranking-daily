package com.song.rerank.config;

import com.song.rerank.domain.dto.JwtUserDto;
import com.song.rerank.security.TokenProvider;
import com.song.rerank.service.UserCacheService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


/**
 * google oauth2
 */
@Component
@AllArgsConstructor
public class OidcSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final UserCacheService userCacheService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        String account = oidcUser.getFullName() + UUID.randomUUID();
        String token = tokenProvider.createToken(account);

        JwtUserDto jwtUserDto = new JwtUserDto();
        jwtUserDto.setUsername(account);
        jwtUserDto.setEmail(oidcUser.getEmail());
        jwtUserDto.setGender(oidcUser.getGender());
        jwtUserDto.setAvatarPath(oidcUser.getPicture());
        jwtUserDto.setAvatarName(oidcUser.getGivenName());
        jwtUserDto.setIsGrant(true);
        jwtUserDto.setToken(token);

        userCacheService.addUserCache(account, jwtUserDto);

        String redirectUrl = "http://localhost:5173/#/login?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
    }
}