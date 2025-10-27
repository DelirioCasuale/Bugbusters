package com.generation.Bugbusters.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
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


    // relazione molti-a-molti con Role

    @ManyToMany(fetch = FetchType.EAGER) // EAGER: serve a caricare i ruoli SEMPRE insieme all'utente
    @JoinTable(
            name = "users_roles", // nome della tabella "ponte"
            joinColumns = @JoinColumn(name = "user_id"), // colonna che punta a questa entità
            inverseJoinColumns = @JoinColumn(name = "role_id") // colonna che punta all'altra entità
    )
    private Set<Role> roles = new HashSet<>();

    // N.B. le relazioni OneToOne con Player e Master sono da aggiungere non appena create quelle entità
}