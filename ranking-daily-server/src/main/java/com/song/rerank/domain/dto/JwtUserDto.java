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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.song.rerank.config.AuthorityDeserializerConfig;
import com.song.rerank.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Data
@AllArgsConstructor
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

    @Override
    @JsonDeserialize(using = AuthorityDeserializerConfig.class)
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (this.isAdmin) {
            return List.of(new SimpleGrantedAuthority("admin"));
        }

        List<SimpleGrantedAuthority> list = roles.stream()
                .flatMap(roleDto -> roleDto.getMenus().stream())
                .map(menuDto -> new SimpleGrantedAuthority(menuDto.getPermission())).toList();

        return list;
    }
}
