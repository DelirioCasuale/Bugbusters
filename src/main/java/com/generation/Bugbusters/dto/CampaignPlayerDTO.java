package com.generation.Bugbusters.dto;

import lombok.Data;

/**
 * DTO che rappresenta un giocatore (e la sua scheda)
 * all'interno della lista di una campagna.
 * (Vista dal Master)
 */
@Data
public class CampaignPlayerDTO {
    
    private Long playerId; // ID dell'User (Player)
    private String username; // Username del Player
    private Long characterId; // ID della Scheda
    private String characterName;
    private String characterClass;
    private int characterLevel;
}