package com.song.rerank.security;

import com.song.rerank.config.properties.SecurityProperties;
import com.song.rerank.utils.EncryptUtils;
import com.song.rerank.utils.RedisUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@AllArgsConstructor
public class TokenProvider {

    private final SecurityProperties properties;
    private final RedisUtils redisUtils;

    public String createToken(Authentication authentication) {
        JwtBuilder jwtBuilder = getJwtBuilder()
                .id(UUID.randomUUID().toString())
                .subject(authentication.getName())
                .claim(properties.getClaimKeyUsername(), authentication.getName());

        jwtBuilder.header()
                .add(properties.getAuthoritiesKey(), authentication.getName());

        return jwtBuilder.compact();
    }

    Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        User principal = new User(claims.getSubject(), "******", new ArrayList<>());
        return new UsernamePasswordAuthenticationToken(principal, token, new ArrayList<>());
    }

    public Claims getClaims(String token) {
        return getJwtParser().parseSignedClaims(token).getPayload();
    }

    public void checkRenewal(String token) {
        long timeMillis = redisUtils.getExpire(properties.getOnlineKey() + token) * 1000;
        Instant now = Instant.now();
        Instant expireInstant = now.plusMillis(timeMillis);
        long differ = expireInstant.toEpochMilli() - now.toEpochMilli();
        if (differ <= properties.getDetect()) {
            long renewMillis = timeMillis + properties.getRenew();
            redisUtils.expire(properties.getOnlineKey() + token, renewMillis, TimeUnit.MILLISECONDS);
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