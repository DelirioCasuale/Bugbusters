package com.generation.Bugbusters.entity;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="roles")
public class Role {

    private Long id;
    private String name;
    @ManyToMany(mappedBy="users")
    @JsonIgnore
    private Map<Long,User> users;

}
