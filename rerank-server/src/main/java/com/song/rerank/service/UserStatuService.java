package com.song.rerank.service;

import com.song.rerank.config.properties.SecurityProperties;
import com.song.rerank.domain.dto.JwtUserDto;
import com.song.rerank.domain.dto.OnlineUserDto;
import com.song.rerank.security.TokenProvider;
import com.song.rerank.utils.EncryptUtils;
import com.song.rerank.utils.RedisUtils;
import com.song.rerank.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *   用戶狀態管理
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserStatuService {

    private final SecurityProperties securityProperties;
    private final TokenProvider tokenProvider;
    private final RedisUtils redisUtils;

    /**
     * 保存在線用戶信息
     * @param jwtUserDto /
     * @param token /
     * @param request /
     */
    public void save(JwtUserDto jwtUserDto, String token, HttpServletRequest request){
        String ip = StringUtils.getIp(request);
        OnlineUserDto onlineUserDto = null;
        try {
            onlineUserDto = new OnlineUserDto(jwtUserDto.getUsername(), ip, EncryptUtils.desEncrypt(token), new Date());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        String loginKey = tokenProvider.loginKey(token);
        redisUtils.set(loginKey, onlineUserDto, securityProperties.getTokenValidityInMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 退出登錄
     * @param token /
     */
    public void logout(String token) {
        String loginKey = tokenProvider.loginKey(token);
        redisUtils.del(loginKey);
    }

    /**
     * 查詢用戶
     * @param key /
     * @return /
     */
    public OnlineUserDto getOne(String key) {
        redisUtils.get(key);
        return (OnlineUserDto)redisUtils.get(key);
    }

    /**
     * 根據用戶名強退用戶
     * @param username /
     */
    @Async
    public void deleteByUsername(String username) {
        String loginKey = securityProperties.getOnlineKey() + username + "*";
        redisUtils.scanDel(loginKey);
    }
}
