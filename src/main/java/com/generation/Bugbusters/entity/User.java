package com.generation.Bugbusters.entity;



import java.sql.Date;
import java.util.List;

import jakarta.persistence.Entity;
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
    private List<Role> roles;
}
