package com.generation.Bugbusters.controller;

import com.generation.Bugbusters.dto.MessageResponse;
import com.generation.Bugbusters.dto.UpdatePasswordRequest;
import com.generation.Bugbusters.dto.UpdateProfileRequest;
import com.generation.Bugbusters.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
// Tutte le API qui richiedono che l'utente sia almeno loggato (ROLE_USER)
@PreAuthorize("hasRole('USER')") 
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        // Gestiamo le eccezioni (BadRequestException) con un @ControllerAdvice
        // Per ora, il service le lancia e Spring le gestirà come 400
        return userService.updateProfile(request);
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        return userService.updatePassword(request);
    }
}