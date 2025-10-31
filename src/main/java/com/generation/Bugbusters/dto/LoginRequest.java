package com.generation.Bugbusters.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank // non può essere nullo o vuoto
    private String username;

    @NotBlank
    private String password;
}