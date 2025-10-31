package com.generation.Bugbusters.dto;

import lombok.Data;

// DTO minimale per rappresentare una scheda in una lista
@Data
public class CharacterSheetSimpleDTO {
    private Long id;
    private String name;
}