package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.CampaignCreateRequest;
import com.generation.Bugbusters.dto.CampaignDTO;
import com.generation.Bugbusters.dto.CampaignStartDateRequest;
import com.generation.Bugbusters.dto.MasterCampaignViewDTO;
import com.generation.Bugbusters.dto.MessageResponse;
import com.generation.Bugbusters.dto.SessionProposalRequest;
import com.generation.Bugbusters.entity.Campaign;
import com.generation.Bugbusters.entity.Master;
import com.generation.Bugbusters.entity.SessionProposal;
import com.generation.Bugbusters.exception.ResourceNotFoundException;
import com.generation.Bugbusters.exception.UnauthorizedException;
import com.generation.Bugbusters.mapper.CampaignMapper;
import com.generation.Bugbusters.repository.CampaignRepository;
import com.generation.Bugbusters.repository.MasterRepository;
import com.generation.Bugbusters.repository.SessionProposalRepository;
import com.generation.Bugbusters.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MasterService {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private MasterRepository masterRepository;

    @Autowired
    private CampaignMapper campaignMapper;

    @Autowired
    private SessionProposalRepository sessionProposalRepository;

    // crea una nuova campagna per il master attualmente loggato
    @Transactional
    public CampaignDTO createCampaign(CampaignCreateRequest dto) {
        // ottiene il profilo Master dell'utente loggato
        Master currentMaster = getCurrentMaster();

        // usa il Mapper per convertire DTO -> Entità e generare i codici
        Campaign newCampaign = campaignMapper.toEntity(dto, currentMaster);

        // salva la nuova campagna
        Campaign savedCampaign = campaignRepository.save(newCampaign);

        // riconverte Entità -> DTO e la restituisce
        return campaignMapper.toDTO(savedCampaign);
    }

    // recupera tutte le campagne gestite dal Master loggato
    @Transactional(readOnly = true)
    public List<CampaignDTO> getMyCampaigns() {
        // ottiene il profilo Master
        Master currentMaster = getCurrentMaster();

        // cerca nel repository usando il metodo custom
        List<Campaign> campaigns = 
                campaignRepository.findByMasterId(currentMaster.getId());

        // mappa la lista di Entità in una lista di DTO
        return campaigns.stream()
                .map(campaignMapper::toDTO)
                .collect(Collectors.toList());
    }

    // metodo helper per ottenere il PROFILO MASTER dell'utente attualmente loggato
    private Master getCurrentMaster() {
        // ottiene l'ID utente dal contesto di sicurezza
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        // cerca il profilo Master (ID Master == ID User)
        return masterRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Profilo Master non trovato per l'utente loggato."));
    }

    // usa l'helper di validazione per ottenere i dettagli di una campagna specifica
    @Transactional(readOnly = true)
    public ResponseEntity<?> getCampaignDetails(Long campaignId) {
        try {
            // ottiene il Master loggato
            Master currentMaster = getCurrentMaster();
            
            // ottiene e valida la campagna con l'helper
            Campaign campaign = getCampaignAndValidateOwnership(campaignId, currentMaster.getId());

            // mappa e restituisce
            MasterCampaignViewDTO dto = campaignMapper.toMasterViewDTO(campaign);
            return ResponseEntity.ok(dto);

        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }

    // Imposta la data di inizio di una campagna.

    @Transactional
    public ResponseEntity<?> setCampaignStartDate(Long campaignId, CampaignStartDateRequest dto) {
        try {
            Master currentMaster = getCurrentMaster();
            Campaign campaign = getCampaignAndValidateOwnership(campaignId, currentMaster.getId());

            campaign.setStartDate(dto.getStartDate());
            campaignRepository.save(campaign);
            
            return ResponseEntity.ok(new MessageResponse("Data d'inizio impostata con successo."));

        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }

    // Crea una nuova proposta di sessione per la votazione.
    @Transactional
    public ResponseEntity<?> proposeSession(Long campaignId, SessionProposalRequest dto) {
        try {
            Master currentMaster = getCurrentMaster();
            Campaign campaign = getCampaignAndValidateOwnership(campaignId, currentMaster.getId());

            // Crea la nuova entità Proposta
            SessionProposal proposal = new SessionProposal();
            proposal.setCampaign(campaign);
            proposal.setProposedDate(dto.getProposedDate());
            
            // Logica delle 48h (come da requisiti)
            proposal.setExpiresOn(LocalDateTime.now().plusHours(48)); 
            proposal.setConfirmed(false);
            
            sessionProposalRepository.save(proposal);

            return new ResponseEntity<>(
                    new MessageResponse("Proposta creata. I giocatori hanno 48 ore per votare."), 
                    HttpStatus.CREATED);

        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }


    // Recupera una campagna e valida che appartenga al master.
    private Campaign getCampaignAndValidateOwnership(Long campaignId, Long masterId)
            throws ResourceNotFoundException, UnauthorizedException {
        
        // Trova la campagna
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Campagna non trovata con ID: " + campaignId));

        // Valida la proprietà
        if (campaign.getMaster() == null || !campaign.getMaster().getId().equals(masterId)) {
            throw new UnauthorizedException(
                    "Non sei autorizzato a modificare questa campagna.");
        }
        
        return campaign;
    }
}