package com.generation.Bugbusters.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// DTO che il player invia per unirsi a una campagna
@Data
public class JoinCampaignRequest {

    @NotBlank(message = "Il codice invito è obbligatorio")
    private String inviteCode; // l'invite_players_code della campagna

    @NotNull(message = "Devi selezionare una scheda personaggio")
    private Long characterSheetId; // l'ID della scheda che il player userà
}