package com.generation.Bugbusters.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

// DTO per impostare la data di inizio della campagna.
@Data
public class CampaignStartDateRequest {
    
    @NotNull(message = "La data è obbligatoria")
    @FutureOrPresent(message = "La data non può essere nel passato")
    private LocalDate startDate;
}