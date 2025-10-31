package com.generation.Bugbusters.dto;

import lombok.Data;

// DTO per mostrare a un Player la campagna a cui si è unito e la scheda specifica che sta usando in quella campagna
@Data
public class JoinedCampaignDTO {
    private Long campaignId;
    private String campaignTitle;
    private CharacterSheetSimpleDTO characterUsed; // La scheda usata
}