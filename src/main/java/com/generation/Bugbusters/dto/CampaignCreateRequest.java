package com.generation.Bugbusters.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// DTO per la CREAZIONE di una nuova campagna
// chiediamo solo titolo e descrizione
@Data
public class CampaignCreateRequest {

    // MODIFICATO: Aggiunti messaggi personalizzati
    @NotBlank(message = "Il titolo è obbligatorio")
    @Size(min = 3, max = 100, message = "Il titolo deve avere tra 3 e 100 caratteri")
    private String title;

    @NotBlank(message = "La descrizione è obbligatoria")
    // Potresti aggiungere un Size anche qui se vuoi un messaggio specifico
    private String description; 
}