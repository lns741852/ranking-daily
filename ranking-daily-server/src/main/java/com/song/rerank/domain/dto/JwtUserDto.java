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
package com.song.rerank.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtUserDto implements UserDetails {

    private Long id;

    private Set<RoleDto> roles;

    private String username;

    private String email;

    private String phone;

    private String gender;

    private String avatarName;

    private String avatarPath;

    private String password;

    private Boolean enabled;

    private Boolean isAdmin = false;

    private Date pwdResetTime;

    private String token;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .flatMap(roleDto -> roleDto.getPermissions().stream())
                .filter(menuDto -> menuDto.getPermission()!= null)
                .map(menuDto -> new SimpleGrantedAuthority(menuDto.getPermission())).toList();
    }
}
