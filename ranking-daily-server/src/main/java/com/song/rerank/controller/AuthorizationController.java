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

import com.song.rerank.annotation.controller.AnonymousDeleteMapping;
import com.song.rerank.annotation.controller.AnonymousPostMapping;
import com.song.rerank.config.properties.LoginProperties;
import com.song.rerank.config.properties.SecurityProperties;
import com.song.rerank.domain.dto.JwtUserDto;
import com.song.rerank.domain.dto.LoginDto;
import com.song.rerank.security.TokenProvider;
import com.song.rerank.service.UserStatuService;

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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 * 授權、根據token獲取用戶詳細信息
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
//@Api(tags = "系統：系統授權接口")
public class AuthorizationController {
    private final SecurityProperties securityProperties;
    private final UserStatuService userStatuService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final  LoginProperties loginProperties;

//    @Log("用戶登入")
//    @ApiOperation("登錄授權")
    @AnonymousPostMapping(value = "/login")
    public ResponseEntity<Object> login(@Validated @RequestBody LoginDto loginDto, HttpServletRequest request) throws Exception {

        //底層使用UserDetail做登入認證
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
        // 返回 token 與 用戶信息
        Map<String, Object> authInfo = new HashMap<String, Object>(2) {{
            put("token", securityProperties.getTokenStartWith() + token);
            put("user", jwtUserDto);
        }};
        if (loginProperties.isSingleLogin()) {
            // 踢掉之前登入的token
            userStatuService.deleteByUsername(loginDto.getUsername());
        }
        // 保存在線信息
        userStatuService.save(jwtUserDto, token, request);
        // 返回登錄信息
        return ResponseEntity.ok(authInfo);
    }

//    @ApiOperation("獲取用戶信息")
    @GetMapping(value = "/info")
    public ResponseEntity<UserDetails> getUserInfo() {
        return ResponseEntity.ok(SecurityUtils.getCurrentUser());
    }


//    @ApiOperation("退出登錄")
    @AnonymousDeleteMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        userStatuService.logout(tokenProvider.getToken(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
