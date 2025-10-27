package com.generation.Bugbusters.entity;

import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="roles")
public class Role {

    private Long id;
    private String name;
    private Map<Long,User> users;

}
