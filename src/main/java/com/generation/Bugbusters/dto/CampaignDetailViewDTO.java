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
    // private Long masterId; // Possiamo aggiungerlo se serve un link al profilo master
    
    // Info Giocatori
    private List<CampaignPlayerDTO> players; // Riutilizziamo il DTO esistente
    
    // Info Calendario (solo per il player)
    // Usiamo il DTO esistente, ma lo carichiamo solo per questa campagna
    private List<PlayerSessionProposalDTO> activeProposals; 
}