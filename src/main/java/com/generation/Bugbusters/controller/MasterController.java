package com.generation.Bugbusters.controller;

import com.generation.Bugbusters.dto.CampaignCreateRequest;
import com.generation.Bugbusters.dto.CampaignDTO;
import com.generation.Bugbusters.dto.CampaignStartDateRequest;
import com.generation.Bugbusters.dto.SessionProposalRequest;
import com.generation.Bugbusters.service.MasterService;
import com.generation.Bugbusters.dto.ClaimCampaignRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master")
// protegge l'intero controller: solo chi ha ROLE_MASTER può entrare
@PreAuthorize("hasRole('MASTER')") 
public class MasterController {

    @Autowired
    private MasterService masterService;

    // endpoint per CREARE una nuova campagna
    // POST /api/master/campaigns
    @PostMapping("/campaigns")
    public ResponseEntity<CampaignDTO> createCampaign(
            @Valid @RequestBody CampaignCreateRequest createRequest) {
        
        CampaignDTO createdCampaign = masterService.createCampaign(createRequest);
        
        // ritorna 201 Created
        return new ResponseEntity<>(createdCampaign, HttpStatus.CREATED);
    }

    // endpoint per LEGGERE tutte le campagne del Master loggato
    // GET /api/master/campaigns
    @GetMapping("/campaigns")
    public ResponseEntity<List<CampaignDTO>> getMyCampaigns() {
        
        List<CampaignDTO> myCampaigns = masterService.getMyCampaigns();
        
        return ResponseEntity.ok(myCampaigns);
    }

    // Endpoint per LEGGERE i dettagli di una campagna specifica (inclusi i giocatori)
    // GET /api/master/campaigns/{id}
    @GetMapping("/campaigns/{id}")
    public ResponseEntity<?> getCampaignDetails(@PathVariable Long id) {
        
        // Delega tutta la logica (inclusa la validazione) al service
        return masterService.getCampaignDetails(id);
    }

    // endpoint per impostare la data d'inizio
    // PATCH /api/master/campaigns/{id}/start-date
    @PatchMapping("/campaigns/{id}/start-date")
    public ResponseEntity<?> setStartDate(
            @PathVariable Long id,
            @Valid @RequestBody CampaignStartDateRequest request) {
        
        return masterService.setCampaignStartDate(id, request);
    }

    // endpoint per proporre una nuova sessione
    // POST /api/master/campaigns/{id}/propose-session
    @PostMapping("/campaigns/{id}/propose-session")
    public ResponseEntity<?> proposeSession(
            @PathVariable Long id,
            @Valid @RequestBody SessionProposalRequest request) {
        
        return masterService.proposeSession(id, request);
    }

    /**
     * endpoint per ESPELLERE un giocatore (la sua scheda) da una campagna
     * DELETE /api/master/campaigns/{campaignId}/players/{characterId}
     */
    @DeleteMapping("/campaigns/{campaignId}/players/{characterId}")
    public ResponseEntity<?> kickPlayer(
            @PathVariable Long campaignId,
            @PathVariable Long characterId) {
        
        return masterService.kickPlayerFromCampaign(campaignId, characterId);
    }

    /**
     * endpoint per reclamare una campagna orfana
     * POST /api/master/campaigns/claim
     */
    @PostMapping("/campaigns/claim")
    public ResponseEntity<?> claimCampaign(
            @Valid @RequestBody ClaimCampaignRequest claimRequest) {
        
        return masterService.claimOrphanedCampaign(claimRequest);
    }
}