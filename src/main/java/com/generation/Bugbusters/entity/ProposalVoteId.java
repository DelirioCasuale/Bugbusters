package com.generation.Bugbusters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
 * questa classe rappresenta la chiave primaria composta della tabella proposal_votes
 * è richiesta da JPA
 * deve implementare serializable e avere equals() e hashCode() (forniti da @Data).
 */
@Embeddable // indica a JPA che questa classe è "incorporabile" come id
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalVoteId implements Serializable {

    @Column(name = "proposal_id")
    private Long proposalId;

    @Column(name = "player_id")
    private Long playerId;
}