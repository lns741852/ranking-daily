package com.song.rerank.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 驗證碼參數配置
 *
 */
@Data
@Configuration
public class LoginProperties {

    private boolean singleLogin = false;
    public static final String cacheKey = "user-login-cache:";

    public boolean isSingleLogin() {
        return singleLogin;
    }

}