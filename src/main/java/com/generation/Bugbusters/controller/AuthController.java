package com.generation.Bugbusters.controller;

import com.generation.Bugbusters.dto.JwtResponse;
import com.generation.Bugbusters.dto.LoginRequest;
import com.generation.Bugbusters.dto.MessageResponse;
import com.generation.Bugbusters.dto.SignupRequest;
import com.generation.Bugbusters.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // tutti gli endpoint qui inizieranno con /api/auth
public class AuthController {

    @Autowired
    private AuthService authService; // inietta il service che fa il lavoro

    
    // endpoint per il LOGIN POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest) {
        
        // delega tutta la logica al service
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        
        // ritorna 200 ok con il DTO JwtResponse nel body
        return ResponseEntity.ok(jwtResponse);
    }

    // endpoint per la REGISTRAZIONE POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody SignupRequest signUpRequest) {
        
        // delega tutta la logica al service
        MessageResponse response = authService.registerUser(signUpRequest);
        
        // ritorna 200 OK con il DTO MessageResponse nel body
        return ResponseEntity.ok(response);
    }
}