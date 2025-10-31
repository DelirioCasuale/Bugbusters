package com.generation.Bugbusters.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
public class Player {

    @Id
    private Long id;


    // relazioni

    @OneToOne(fetch = FetchType.LAZY) // LAZY: non carica user finché non serve esplicitamente
    @MapsId // serve per dire che l'id di user è l'id per questa entità
    @JoinColumn(name = "user_id") // specifica la colonna che è sia PK che FK
    private User user;

    @OneToMany(
            mappedBy = "player", // player è il nome del campo in characterSheet
            cascade = CascadeType.ALL, // se elimino il player, elimino le sue schede
            orphanRemoval = true // se rimuovo una scheda dalla lista, eliminala
    )
    private Set<CharacterSheet> characterSheets = new HashSet<>();

    // N.B. da aggiungere qui la relazione con i voti (FORSE)
}