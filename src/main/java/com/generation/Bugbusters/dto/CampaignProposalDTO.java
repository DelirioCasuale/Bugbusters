package com.generation.Bugbusters.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CampaignProposalDTO {
    private Long id;
    private LocalDateTime proposedDate;
    private LocalDateTime expiresOn;
    private boolean isConfirmed;
    private int voteCount; // Quanti hanno votato
}