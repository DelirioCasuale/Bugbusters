package com.generation.Bugbusters.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate; // LocalDate per i campi DATE (quindi solo data, niente ora)

    @Column(name = "scheduled_next_session")
    private LocalDate scheduledNextSession;

    @Column(name = "invite_players_code", unique = true, length = 10)
    private String invitePlayersCode;

    @Column(name = "invite_masters_code", unique = true, length = 10)
    private String inviteMastersCode;

    // colonna per la logica di ban del master dalla campagna
    @Column(name = "master_ban_pending_until")
    private LocalDateTime masterBanPendingUntil;


    // relazioni

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id") // nello script sql, master_id può essere NULL
    private Master master; // molte campagne possono appartenere a 1 Master

    /*
     * questa è la relazione n-n tra campaign e characterSheet
     * la tabella ponte è campaign_players
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "campaign_players", // tabella ponte
            joinColumns = @JoinColumn(name = "campaign_id"), // FK verso questa entità
            inverseJoinColumns = @JoinColumn(name = "character_id") // FK verso l'altra entità
    )
    private Set<CharacterSheet> players = new HashSet<>(); // i giocatori (personaggi) in campagna

    // relazione 1-n con le sessioni storiche (per il riassunto)
    @OneToMany(
            mappedBy = "campaign",
            cascade = CascadeType.ALL, // se cancello la campagna, cancello le sessioni
            orphanRemoval = true
    )
    private Set<CampaignSession> sessions = new HashSet<>();

    
    // relazione 1-n con le proposte di sessione (per i voti)
    @OneToMany(
            mappedBy = "campaign",
            cascade = CascadeType.ALL, // se cancello la campagna, cancello le proposte
            orphanRemoval = true
    )
    private Set<SessionProposal> proposals = new HashSet<>();
}