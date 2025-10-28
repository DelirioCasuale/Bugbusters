package com.generation.Bugbusters.dto;

import com.generation.Bugbusters.enumeration.Race;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


// DTO snello per la CREAZIONE di una nuova scheda personaggio
// chiediamo solo il minimo indispensabile per creare la scheda
@Data
public class CharacterSheetCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String primaryClass;
    
    @NotNull // per le Enum si usa @NotNull
    private Race race;

    // tutti gli altri campi (statistiche, ecc.) avranno un valore di default
    // o potranno essere modificati in un secondo momento.
}