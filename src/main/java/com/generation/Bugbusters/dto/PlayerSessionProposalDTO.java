package com.generation.Bugbusters.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO per mostrare a un Player una proposta di sessione.
 * Se ha votato o meno per la proposta viene indicato nel campo hasVoted
 */
@Data
public class PlayerSessionProposalDTO {
    
    private Long proposalId;
    private Long campaignId;
    private String campaignTitle;
    private LocalDateTime proposedDate;
    private LocalDateTime expiresOn; // Quando scade il voto
    private boolean isConfirmed;
    
    // Campo chiave: dice al frontend se mostrare "Vota" o "Votato" o simili
    private boolean hasVoted; 
}