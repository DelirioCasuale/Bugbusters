package com.generation.Bugbusters.dto;

import lombok.Data;

import java.time.LocalDate;

// DTO per la VISUALIZZAZIONE di una campagna
// mostra i dati principali, inclusi i codici d'invito
@Data
public class CampaignDTO {
    
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private String invitePlayersCode;
    private String inviteMastersCode;
    
}