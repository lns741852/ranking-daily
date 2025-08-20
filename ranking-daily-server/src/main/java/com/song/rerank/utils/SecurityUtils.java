package com.song.rerank.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.song.rerank.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class SecurityUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 獲取當前登錄的用戶
     */
    public static UserDetails getCurrentUser() {
        UserDetailsService userDetailsService = SpringContextHolder.getBean(UserDetailsService.class);
        return userDetailsService.loadUserByUsername(getCurrentUsername());
    }

    /**
     * 獲取系統用戶名稱
     */
    public static String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "當前登錄狀態過期");
        }
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        throw new BadRequestException(HttpStatus.UNAUTHORIZED, "找不到當前登錄的信息");
    }

    /**
     * 獲取系統用戶ID
     */
    public static Long getCurrentUserId() {
        UserDetails userDetails = getCurrentUser();
        try {
            Map<?, ?> map = objectMapper.convertValue(userDetails, Map.class);
            Map<?, ?> userMap = (Map<?, ?>) map.get("user");
            return userMap != null ? Long.parseLong(userMap.get("id").toString()) : null;
        } catch (Exception e) {
            log.error("轉換使用者 ID 失敗", e);
            return null;
        }
    }

    /**
     * 獲取當前用戶的數據權限
     */
    public static List<Long> getCurrentUserDataScope() {
        UserDetails userDetails = getCurrentUser();
        try {
            Map<?, ?> map = objectMapper.convertValue(userDetails, Map.class);
            Object scopes = map.get("dataScopes");
            return objectMapper.convertValue(scopes, objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
        } catch (Exception e) {
            log.error("轉換 DataScope 失敗", e);
            return Collections.emptyList();
        }
    }
}
