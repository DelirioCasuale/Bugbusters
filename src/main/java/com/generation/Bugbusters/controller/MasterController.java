package com.generation.Bugbusters.controller;

import com.generation.Bugbusters.dto.CampaignCreateRequest;
import com.generation.Bugbusters.dto.CampaignDTO;
import com.generation.Bugbusters.service.MasterService;
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
}