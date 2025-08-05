package com.song.rerank.service;


import com.song.rerank.domain.Menu;
import com.song.rerank.domain.Role;
import com.song.rerank.domain.dto.AuthorityDto;
import com.song.rerank.domain.dto.UserDto;
import com.song.rerank.repo.RoleRepo;
import com.song.rerank.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "role")
public class RoleService {

    private final RoleRepo roleRepo;

    @Cacheable(key = "'auth:' + #p0.id")
    public List<AuthorityDto> mapToGrantedAuthorities(UserDto user) {
        Set<String> permissions = new HashSet<>();
        // 如果是管理員直接返回
        if (user.getIsAdmin()) {
            permissions.add("admin");
            return permissions.stream().map(AuthorityDto::new)
                    .collect(Collectors.toList());
        }
        Set<Role> roles = roleRepo.findByUserId(user.getId());
        permissions = roles.stream().flatMap(role -> role.getMenus().stream())
                .map(Menu::getPermission)
                .filter(StringUtils::isNotBlank).collect(Collectors.toSet());
        return permissions.stream().map(AuthorityDto::new)
                .collect(Collectors.toList());
    }
}
