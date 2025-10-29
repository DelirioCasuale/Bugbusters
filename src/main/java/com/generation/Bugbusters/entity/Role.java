package com.generation.Bugbusters.entity;

import com.generation.Bugbusters.enumeration.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles") // mappa questa classe alla tabella 'roles' nel database
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING) // serve per indicare a JPA di salvare l'enum come stringa
    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private RoleName roleName;
}