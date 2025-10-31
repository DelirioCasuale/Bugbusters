package com.generation.Bugbusters.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CampaignDetailViewDTO {
    // Info Campagna
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate scheduledNextSession;
    
    // Info Master
    private String masterUsername;
    
    // Info Giocatori
    private List<CampaignPlayerDTO> players; // (Questo nome è corretto)
    
    // --- MODIFICATO ---
    // Info Calendario
    private List<PlayerSessionProposalDTO> activeProposals; // Solo quelle attive/votabili
    private List<PlayerSessionProposalDTO> pastProposals;   // Quelle scadute, confermate, o già votate
}