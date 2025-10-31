package com.generation.Bugbusters.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// DTO per la CREAZIONE di una nuova campagna
// chiediamo solo titolo e descrizione
@Data
public class CampaignCreateRequest {

    @NotBlank(message = "Il titolo è obbligatorio")
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank(message = "La descrizione è obbligatoria")
    private String description;
}