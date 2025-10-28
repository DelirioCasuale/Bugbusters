package com.generation.Bugbusters.controller;

import com.generation.Bugbusters.dto.CharacterSheetCreateRequest;
import com.generation.Bugbusters.dto.CharacterSheetDTO;
import com.generation.Bugbusters.dto.JoinCampaignRequest;
import com.generation.Bugbusters.dto.JoinedCampaignDTO;
import com.generation.Bugbusters.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/player")
// protegge l'intero controller: solo chi ha ROLE_PLAYER può entrare
@PreAuthorize("hasRole('PLAYER')") 
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    
    // Endpoint per CREARE una nuova scheda POST /api/player/sheets
    @PostMapping("/sheets")
    public ResponseEntity<CharacterSheetDTO> createSheet(
            @Valid @RequestBody CharacterSheetCreateRequest createRequest) {
        
        CharacterSheetDTO createdSheet = playerService.createCharacterSheet(createRequest);
        
        // ritorna 201 Created con la nuova scheda nel body
        return new ResponseEntity<>(createdSheet, HttpStatus.CREATED);
    }

    // Endpoint per LEGGERE tutte le schede del giocatore loggato GET /api/player/sheets
    @GetMapping("/sheets")
    public ResponseEntity<List<CharacterSheetDTO>> getMySheets() {
        
        List<CharacterSheetDTO> mySheets = playerService.getAllMyCharacterSheets();
        
        return ResponseEntity.ok(mySheets);
    }

    // endpoint per UNIRSI a una campagna
    // POST /api/player/campaigns/join
    @PostMapping("/campaigns/join")
    public ResponseEntity<?> joinCampaign(
            @Valid @RequestBody JoinCampaignRequest joinRequest) {
        
        // il controller delega tutto al service e restituisce la ResponseEntity che il service ha preparato
        return playerService.joinCampaign(joinRequest);
    }

    // endpoint per LEGGERE tutte le campagne a cui il giocatore loggato si è unito
    // GET /api/player/campaigns/joined
    @GetMapping("/campaigns/joined")
    public ResponseEntity<List<JoinedCampaignDTO>> getMyJoinedCampaigns() {
        List<JoinedCampaignDTO> campaigns = playerService.getMyJoinedCampaigns();
        return ResponseEntity.ok(campaigns);
    }
}