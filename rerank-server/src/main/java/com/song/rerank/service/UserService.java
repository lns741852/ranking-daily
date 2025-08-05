package com.song.rerank.service;

import com.song.rerank.domain.User;
import com.song.rerank.domain.dto.UserDto;
import com.song.rerank.exception.EntityNotFoundException;
import com.song.rerank.mapper.UserMapper;
import com.song.rerank.repo.UserRepo;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "user")
public class UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;

    public UserDto getLoginData(String userName) {
        User user = userRepo.findByUsername(userName);
        if (user == null) {
            throw new EntityNotFoundException(User.class, "name", userName);
        } else {
            return userMapper.toDto(user);
        }
    }

}
