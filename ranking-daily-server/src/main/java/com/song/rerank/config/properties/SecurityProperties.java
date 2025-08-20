package com.song.rerank.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class SecurityProperties {

    /** HTTP 請求中的 Header 名稱，一般為 Authorization */
    private String header;

    /** Token 前綴，例如 "Bearer "（注意結尾有空格） */
    private String tokenStartWith;

    /** Base64 編碼的密鑰，長度需至少 88 位 */
    private String base64Secret;

    /** Token 過期時間（單位：毫秒） */
    private Long tokenValidityInMillis;

    /** Redis 中儲存線上使用者資料的 Key 前綴 */
    private String onlineKey;

    /** 驗證碼存放於 Redis 中的 Key 前綴 */
    private String codeKey;

    /** Token 自動續期的檢查間隔（單位：毫秒） */
    private Long detect;

    /** Token 續期的有效時長（單位：毫秒） */
    private Long renew;

    /** JWT 中存放使用者名稱的 Claim Key */
    private String claimKeyUsername;

    /** JWT 中存放權限資訊的 Claim Key */
    private String authoritiesKey;

    /**
     * 取得完整的 Token 前綴，例如 "Bearer "
     * @return 加上空格的前綴
     */
    public String getTokenStartWith() {
        return tokenStartWith + " ";
    }
}