package com.song.rerank.controller;

import com.song.rerank.annotation.controller.AnonymousGetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;


@RestController
public class AuthController {

    @AnonymousGetMapping("/auth/me")
    public Map<String, Object> me(@AuthenticationPrincipal OidcUser user) {
        if (user == null) return Map.of("anonymous", true);
        return Map.of(
                "sub", user.getSubject(),
                "email", user.getEmail(),
                "name", user.getFullName(),
                "claims", user.getClaims()
        );
    }
}