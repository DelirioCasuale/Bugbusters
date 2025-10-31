package com.generation.Bugbusters.controller;

import com.generation.Bugbusters.dto.CharacterSheetCreateRequest;
import com.generation.Bugbusters.dto.CharacterSheetDTO;
import com.generation.Bugbusters.dto.JoinCampaignRequest;
import com.generation.Bugbusters.dto.JoinedCampaignDTO;
import com.generation.Bugbusters.dto.PlayerSessionProposalDTO;
import com.generation.Bugbusters.service.PlayerService;
import com.generation.Bugbusters.dto.OrphanedCampaignDTO;
import com.generation.Bugbusters.dto.CampaignDetailViewDTO;
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

    /**
     * endpoint per vedere tutte le proposte di sessione attive per le campagne del giocatore
     * GET /api/player/proposals
     */
    @GetMapping("/proposals")
    public ResponseEntity<List<PlayerSessionProposalDTO>> getActiveProposals() {
        List<PlayerSessionProposalDTO> proposals = playerService.getActiveProposals();
        return ResponseEntity.ok(proposals);
    }

    /**
     * endpoint per votare una proposta
     * POST /api/player/proposals/{id}/vote
     */
    @PostMapping("/proposals/{id}/vote")
    public ResponseEntity<?> voteForProposal(@PathVariable Long id) {
        return playerService.voteForProposal(id);
    }

    /**
     * endpoint per VEDERE le campagne orfane(con master bannato)
     * e il codice per invitare un nuovo master
     * GET /api/player/campaigns/orphaned
     */
    @GetMapping("/campaigns/orphaned")
    public ResponseEntity<List<OrphanedCampaignDTO>> getOrphanedCampaigns() {
        List<OrphanedCampaignDTO> campaigns = playerService.getMyOrphanedCampaigns();
        return ResponseEntity.ok(campaigns);
    }

    /**
     * Endpoint per OTTENERE i dati completi di UNA singola scheda.
     * GET /api/player/sheets/{id}
     */
    @GetMapping("/sheets/{id}")
    public ResponseEntity<?> getCharacterSheetById(@PathVariable Long id) {
        return playerService.getCharacterSheetDetails(id);
    }
    
    /**
     * Endpoint per AGGIORNARE una scheda personaggio esistente.
     * PUT /api/player/sheets/{id}
     */
    @PutMapping("/sheets/{id}")
    public ResponseEntity<?> updateCharacterSheet(
            @PathVariable Long id,
            @Valid @RequestBody CharacterSheetDTO sheetDTO) {
        
        return playerService.updateCharacterSheet(id, sheetDTO);
    }

    /**
     * Endpoint per OTTENERE i dati completi di UNA singola campagna
     * (vista del giocatore).
     * GET /api/player/campaigns/{id}
     */
    @GetMapping("/campaigns/{id}")
    public ResponseEntity<?> getCampaignDetailsForPlayer(@PathVariable Long id) {
        return playerService.getCampaignDetailsForPlayer(id);
    }
}