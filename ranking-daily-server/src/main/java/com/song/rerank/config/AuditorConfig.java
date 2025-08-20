package com.song.rerank.config;

import com.song.rerank.utils.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class AuditorConfig implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            return Optional.of(SecurityUtils.getCurrentUsername());
        }catch (Exception ignored){}
        return Optional.of("System");
    }
}