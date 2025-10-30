package com.generation.Bugbusters.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCampaignRequest {

    @NotBlank(message = "Il titolo è obbligatorio")
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank(message = "La descrizione è obbligatoria")
    private String description;
}