package com.generation.Bugbusters.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "masters")
@Getter
@Setter
@NoArgsConstructor
public class Master {

    @Id
    private Long id;


    // relazioni

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // anche qua l'id del master è l'id del suo user
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(
            mappedBy = "master", // master è il nome del campo in campaign
            cascade = CascadeType.ALL, // se elimino il master, elimino le sue campagne
            orphanRemoval = true // se rimuovo una campagna dalla lista, eliminala
    )
    private Set<Campaign> campaigns = new HashSet<>();

    // N.B. l'entità campaign non l'ho ancora implementata, ma possiamo già definire la relazione
}