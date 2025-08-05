package com.song.rerank.service;


import com.song.rerank.config.properties.LoginProperties;
import com.song.rerank.domain.dto.JwtUserDto;
import com.song.rerank.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


/**
 *  用戶緩存
 **/
@Component
public class UserCacheService {

    @Autowired
    private RedisUtils redisUtils;
    @Value("${login.user-cache.idle-time}")
    private long idleTime;

    public JwtUserDto getUserCache(String userName) {
        if (StringUtils.isNotEmpty(userName)) {
            // 獲取數據
            Object obj = redisUtils.get(LoginProperties.cacheKey + userName);
            if(obj != null){
                return (JwtUserDto)obj;
            }
        }
        return null;
    }

    /**
     *  添加緩存Redis
     * @param userName 用戶名
     */
    @Async
    public void addUserCache(String userName, JwtUserDto user) {
        if (StringUtils.isNotEmpty(userName)) {
            // 添加数据, 避免数据同时过期
            long time = (long) (idleTime + Math.random()*1000);
            redisUtils.set(LoginProperties.cacheKey + userName, user, time);
        }
    }

    /**
     * 清理用戶緩存訊息
     * @param userName 用戶名
     */
    @Async
    public void cleanUserCache(String userName) {
        if (StringUtils.isNotEmpty(userName)) {
            // 清除數據
            redisUtils.del(LoginProperties.cacheKey + userName);
        }
    }
}