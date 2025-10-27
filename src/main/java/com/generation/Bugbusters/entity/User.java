package com.generation.Bugbusters.entity;



import java.sql.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name="users")
@EqualsAndHashCode
public abstract class User {

    private Long id;
    private String username;
    private String password;
    private Date created_at;
    @ManyToMany
    @JoinTable
    (
        name="users_roles",
        joinColumns = {@JoinColumn(name="user_id")},
        inverseJoinColumns = {@JoinColumn(name="role_id")}
    )
    private List<Role> roles;
}
