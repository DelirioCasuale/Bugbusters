package com.generation.Bugbusters.dto;

import lombok.Data;
import java.util.List;


// la risposta inviata dopo un login andato a buon fine contiene il token JWT e alcune info sull'utente
@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer"; // standard
    private Long id;
    private String username;
    private String email;
    private List<String> roles; // lista dei ruoli ["ROLE_USER", "ROLE_ADMIN"]

    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}