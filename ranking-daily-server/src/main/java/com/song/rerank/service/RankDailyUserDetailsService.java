package com.song.rerank.service;

import com.song.rerank.domain.User;
import com.song.rerank.domain.dto.JwtUserDto;
import com.song.rerank.repo.UserRepo;
import com.song.rerank.utils.OrikaUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 實作UserDetailsService
 */
@Slf4j
@RequiredArgsConstructor
@Service("userDetailsService")
public class RankDailyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;
    private final UserCacheService userCacheService;

    @Override
    public JwtUserDto loadUserByUsername(String username) {
        JwtUserDto jwtUserDto = userCacheService.getUserCache(username);
        if(jwtUserDto == null){
            User user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("找不到用戶"));
            jwtUserDto = OrikaUtils.map(JwtUserDto.class,user);
            userCacheService.addUserCache(username, jwtUserDto);
        }
        return jwtUserDto;
    }
}
