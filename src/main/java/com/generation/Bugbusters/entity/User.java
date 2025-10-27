package com.generation.Bugbusters.entity;



import java.sql.Date;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public abstract class User {

    private Long id;
    private String username;
    private String password;
    Date created_at;
}
