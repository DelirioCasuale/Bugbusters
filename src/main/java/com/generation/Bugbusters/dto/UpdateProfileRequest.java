package com.generation.Bugbusters.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    
    @NotBlank
    @Size(min = 3, max = 50)
    private String newUsername;

    @NotBlank
    @Size(max = 100)
    @Email
    private String newEmail;

    // Non @NotBlank, l'URL è opzionale
    private String newImageUrl;
}