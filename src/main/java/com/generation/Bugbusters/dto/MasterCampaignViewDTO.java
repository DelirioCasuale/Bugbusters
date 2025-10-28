package com.generation.Bugbusters.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO per la vista "dettaglio" di una campagna,
 * riservata al Master. Include la lista dei giocatori.
 */
@Data
public class MasterCampaignViewDTO {
    
    // Dati base della campagna (simili a CampaignDTO)
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private String invitePlayersCode;
    private String inviteMastersCode;
    
    // Dati arricchiti
    private List<CampaignPlayerDTO> players;
}