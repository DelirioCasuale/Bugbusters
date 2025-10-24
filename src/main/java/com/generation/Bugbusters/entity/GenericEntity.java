package com.generation.Bugbusters.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public abstract class GenericEntity {

    private Long id;

}
