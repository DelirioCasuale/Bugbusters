package com.generation.Bugbusters.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO che un master invia per reclamare una campagna orfana usando il codice d'invito speciale

@Data
public class ClaimCampaignRequest {

    @NotBlank(message = "Il codice invito master è obbligatorio")
    private String inviteMastersCode;
}