package com.song.rerank.security;

import com.song.rerank.properties.SecurityProperties;
import com.song.rerank.service.UserCacheService;
import com.song.rerank.utils.StringUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * OncePerRequestFilter 適用 security中，防止被多次執行
 */
//@Component
@AllArgsConstructor
public class TokenFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(TokenFilter.class);
    private final TokenProvider tokenProvider;
    private final SecurityProperties properties;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (StringUtil.isNotBlank(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            tokenProvider.renewToken(token);
        }
        filterChain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(properties.getHeader());
        if (StringUtil.isNotBlank(bearerToken) && bearerToken.startsWith(properties.getTokenStartWith())) {
            return bearerToken.replace(properties.getTokenStartWith(), "");
        }
        log.debug("非法Token：{}", bearerToken);
        return null;
    }


}
