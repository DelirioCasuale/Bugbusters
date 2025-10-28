package com.generation.Bugbusters.dto;

import lombok.Data;

/**
 * DTO per rappresentare un utente nella dashboard dell'admin
 */
@Data
public class AdminUserViewDTO {
    
    private Long id;
    private String username;
    private String email;
    private boolean isBanned;
    
    // ruoli "dinamici" per la visualizzazione nella dashboard
    private boolean isAdmin;
    private boolean isPlayer;
    private boolean isMaster;
}