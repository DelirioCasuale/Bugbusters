package com.generation.Bugbusters.dto;

import lombok.Data;

@Data
public class AdminUserUpdateDTO {
    private String username;
    private String email;
    private String profileImageUrl;
    private boolean isAdmin; // Per promuovere/declassare
    // I ruoli Master/Player non sono direttamente modificabili qui
}