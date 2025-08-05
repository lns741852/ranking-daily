package com.song.rerank.service;

import com.song.rerank.domain.dto.JwtUserDto;
import com.song.rerank.domain.dto.UserDto;
import com.song.rerank.exception.BadRequestException;
import jakarta.persistence.EntityNotFoundException;
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
public class MyUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final UserCacheService userCacheService;

    @Override
    public JwtUserDto loadUserByUsername(String username) {
        JwtUserDto jwtUserDto = userCacheService.getUserCache(username);
        if(jwtUserDto == null){
            UserDto user;
            try {
                user = userService.getLoginData(username);
            } catch (EntityNotFoundException e) {
                throw new UsernameNotFoundException(username, e);
            }
            if (user == null) {
                throw new UsernameNotFoundException("");
            } else {
                if (!user.getEnabled()) {
                    throw new BadRequestException("帳號未啟用！");
                }
                jwtUserDto = new JwtUserDto(user.getId(),user.getRoles(), user.getUsername(), null,null,null,null,null, user.getPassword(), null,true,null);
                // 添加緩存數據
                userCacheService.addUserCache(username, jwtUserDto);
            }
        }
        return jwtUserDto;
    }
}
