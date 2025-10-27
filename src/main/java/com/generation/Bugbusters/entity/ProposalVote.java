package com.generation.Bugbusters.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proposal_votes")
@Data
@NoArgsConstructor
public class ProposalVote {

    /*
     * questa entità usa una chiave primaria composta
     * c'é necessità di una classe @Embeddable per rappresentarla
     */
    @EmbeddedId
    private ProposalVoteId id;


    // relazioni
    // Definiamo le due parti della chiave composta come relazioni

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("proposalId") // mappa la parte proposalId della chiave composita
    @JoinColumn(name = "proposal_id")
    private SessionProposal proposal;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("playerId") // mappa la parte playerId della chiave composita
    @JoinColumn(name = "player_id")
    private Player player;
}