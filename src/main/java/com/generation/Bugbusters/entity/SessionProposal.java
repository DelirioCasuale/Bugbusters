package com.generation.Bugbusters.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "session_proposals")
@Getter
@Setter
@NoArgsConstructor
public class SessionProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proposed_date", nullable = false)
    private LocalDateTime proposedDate; // meglio qua LocalDateTime per data e ora

    @Column(name = "expires_on", nullable = false)
    private LocalDateTime expiresOn; // per la logica delle 48h di votazione altrimenti scelta automatica

    @Column(name = "is_confirmed")
    private boolean isConfirmed = false;


    // relazioni

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    /*
     * relazione 1-n con i voti
     * ProposalVote è l'entità che rappresenta la tabella ponte proposal_votes
     */
    @OneToMany(
            mappedBy = "proposal",
            cascade = CascadeType.ALL, // se cancello la proposta, cancello i voti
            orphanRemoval = true
    )
    private Set<ProposalVote> votes = new HashSet<>();
}