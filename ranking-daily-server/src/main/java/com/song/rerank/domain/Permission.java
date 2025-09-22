package com.song.rerank.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;


@Entity
@Getter
@Setter
@Table(name = "sys_permission")
public class Permission extends BaseEntity implements Serializable {

    @Id
    @Column(name = "permission_id")
    @NotNull(groups = {Update.class})
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;

    private String permission;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}