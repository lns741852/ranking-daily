package com.song.rerank.security;

import com.song.rerank.properties.SecurityProperties;
import com.song.rerank.utils.EncryptUtils;
import com.song.rerank.utils.RedisUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@AllArgsConstructor
public class TokenProvider {

    private final SecurityProperties properties;
    private final RedisUtils redisUtils;

    public String createToken( String username) {
        return getJwtBuilder().subject(username).compact();
    }

    Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        User principal = new User(claims.getSubject(), "******", new ArrayList<>());
        return new UsernamePasswordAuthenticationToken(principal, token, new ArrayList<>());
    }

    public Claims getClaims(String token) {
        return getJwtParser().parseSignedClaims(token).getPayload();
    }

    public void renewToken(String token) {
        long expireTimeMs = redisUtils.getExpire(properties.getOnlineKey() + token) * 1000;
        Instant now = Instant.now();
        Instant expireInstant = now.plusMillis(expireTimeMs);
        long differ = expireInstant.toEpochMilli() - now.toEpochMilli();
        if (differ <= properties.getDetect()) {
            long newExpireTimeMs = expireTimeMs + properties.getRenew();
            redisUtils.expire(properties.getOnlineKey() + token, newExpireTimeMs, TimeUnit.MILLISECONDS);
        }
    }

    public String getToken(HttpServletRequest request) {
        final String requestHeader = request.getHeader(properties.getHeader());
        if (requestHeader != null && requestHeader.startsWith(properties.getTokenStartWith())) {
            return requestHeader.substring(7);
        }
        return null;
    }

    public String loginKey(String token) {
        Claims claims = getClaims(token);
        String md5Token = EncryptUtils.md5Hex(token);
        return properties.getOnlineKey() + claims.getSubject() + "-" + md5Token;
    }


    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.getBase64Secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private JwtParser getJwtParser() {
        return Jwts.parser().verifyWith(getSecretKey()).build();
    }

    private JwtBuilder getJwtBuilder() {
        return Jwts.builder().signWith(getSecretKey(), Jwts.SIG.HS512);
    }

}