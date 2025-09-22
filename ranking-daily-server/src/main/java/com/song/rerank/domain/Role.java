package com.song.rerank.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * 角色
 * @author Zheng Jie
 * @date 2018-11-22
 */
@Getter
@Setter
@Entity
@Table(name = "sys_role")
public class Role extends BaseEntity implements Serializable {

    @Id
    @Column(name = "role_id")
    @NotNull(groups = {Update.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "sys_roles_permissions",
            joinColumns = {@JoinColumn(name = "role_id",referencedColumnName = "role_id")},
            inverseJoinColumns = {@JoinColumn(name = "permission_id",referencedColumnName = "permission_id")})
    private Set<Permission> permissions;

    @NotBlank
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}