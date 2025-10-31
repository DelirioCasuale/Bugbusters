package com.generation.Bugbusters.controller;

import com.generation.Bugbusters.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /*
     * endpoint per diventare un player
     * richiede che l'utente sia loggato
     * POST /api/profile/become-player
     * così da assegnargli il ruolo di Player
     */
    @PostMapping("/become-player")
    @PreAuthorize("hasRole('USER')") 
    public ResponseEntity<?> becomePlayer() {
        return profileService.becomePlayer(); // Restituisce direttamente la risposta del service
    }

    /*
     * endpoint per diventare un Master
     * richiede che l'utente sia loggato
     * POST /api/profile/become-master
     * così da assegnargli il ruolo di Master
     */
    @PostMapping("/become-master")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> becomeMaster() {
        return profileService.becomeMaster(); // Restituisce direttamente la risposta del service
    }
}