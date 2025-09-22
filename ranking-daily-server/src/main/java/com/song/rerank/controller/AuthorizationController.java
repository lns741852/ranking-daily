/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.song.rerank.controller;

import com.song.rerank.properties.LoginProperties;
import com.song.rerank.properties.SecurityProperties;
import com.song.rerank.domain.dto.JwtUserDto;
import com.song.rerank.domain.dto.LoginDto;
import com.song.rerank.security.TokenProvider;

import com.song.rerank.utils.RedisUtils;
import com.song.rerank.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 * 授權、根據token獲取用戶詳細信息
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
//@Api(tags = "系統：系統授權接口")
public class AuthorizationController {
    private final SecurityProperties securityProperties;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final  LoginProperties loginProperties;

    private final  RedisUtils redisUtils;

//    @Log("用戶登入")
//    @ApiOperation("登錄授權")
    @PostMapping(value = "/login")
    public ResponseEntity<JwtUserDto> login(@Validated @RequestBody LoginDto loginDto, HttpServletRequest request) {
        //底層使用UserDetail做登入認證
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication.getName());
        JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
        jwtUserDto.setToken(securityProperties.getTokenStartWith() + token);
        // 返回登錄信息
        return ResponseEntity.ok(jwtUserDto);
    }

//    @ApiOperation("獲取用戶信息")
    @GetMapping(value = "/info")
    public ResponseEntity<UserDetails> getUserInfo() {
        return ResponseEntity.ok(SecurityUtils.getCurrentUser());
    }


//    @ApiOperation("退出登錄")
    @DeleteMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
//        userStatuService.logout(tokenProvider.getToken(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping(value = "/clearRedisCache")
    public ResponseEntity<Object> clearRedisCache(HttpServletRequest request) {
        redisUtils.clearRedisCache();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
