package com.generation.Bugbusters.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // usare Long per gli id principali è più robusto in termini di scalabilità

    @Column(nullable = false, unique = true, length = 50)
    private String username; // nome utente unico

    @Column(nullable = false, unique = true, length = 100)
    private String email; // email unica

    @Column(name = "password_hash", nullable = false)
    private String passwordHash; // bisogno di mappare snake_case (DB) a camelCase (Java)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // colonne per la logica di ban
    @Column(name = "is_banned")
    private boolean isBanned = false;

    @Column(name = "deletion_scheduled_on")
    private LocalDateTime deletionScheduledOn;

    // relazioni

    // relazione molti-a-molti con role
    @ManyToMany(fetch = FetchType.EAGER) // EAGER: serve a caricare i ruoli SEMPRE insieme all'utente
    @JoinTable(
            name = "users_roles", // nome della tabella "ponte"
            joinColumns = @JoinColumn(name = "user_id"), // colonna che punta a questa entità
            inverseJoinColumns = @JoinColumn(name = "role_id") // colonna che punta all'altra entità
    )
    private Set<Role> roles = new HashSet<>();

    // relazione uno-a-uno con player
    @OneToOne(
            mappedBy = "user", // user è il nome del campo in player
            cascade = CascadeType.ALL, // se elimino user, elimino il suo profilo player
            orphanRemoval = true, // rimuovi il profilo se non è più associato
            fetch = FetchType.LAZY // non caricare il profilo finché non serve esplicitamente
    )
    private Player player; // profilo player associato

    @OneToOne(
            mappedBy = "user", // user è il nome del campo in master
            cascade = CascadeType.ALL, // se elimino user, elimino il suo profilo master
            orphanRemoval = true, // rimuovi il profilo se non è più associato
            fetch = FetchType.LAZY // non caricare il profilo finché non serve esplicitamente
    )
    private Master master; // profilo master associato
}