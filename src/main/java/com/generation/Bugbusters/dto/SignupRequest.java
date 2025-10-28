package com.generation.Bugbusters.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(max = 100)
    @Email // controlla che sia un formato email valido
    private String email;

    @NotBlank
    @Size(min = 6, max = 255) // min 6 per sicurezza base
    private String password;
}