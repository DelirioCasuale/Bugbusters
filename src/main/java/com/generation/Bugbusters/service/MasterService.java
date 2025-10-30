package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.CampaignCreateRequest;
import com.generation.Bugbusters.dto.CampaignDTO;
import com.generation.Bugbusters.dto.CampaignProposalDTO;
import com.generation.Bugbusters.dto.CampaignStartDateRequest;
import com.generation.Bugbusters.dto.MasterCampaignViewDTO;
import com.generation.Bugbusters.dto.MessageResponse;
import com.generation.Bugbusters.dto.SessionProposalRequest;
import com.generation.Bugbusters.dto.UpdateCampaignRequest;
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
import com.generation.Bugbusters.entity.CharacterSheet;
import com.generation.Bugbusters.exception.BadRequestException;
import com.generation.Bugbusters.repository.CharacterSheetRepository;
import com.generation.Bugbusters.dto.ClaimCampaignRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MasterService {

    private static final Logger logger = LoggerFactory.getLogger(MasterService.class);

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private MasterRepository masterRepository;

    @Autowired
    private CampaignMapper campaignMapper;

    @Autowired
    private SessionProposalRepository sessionProposalRepository;

    @Autowired
    private CharacterSheetRepository characterSheetRepository;

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
            Master currentMaster = getCurrentMaster();
            Campaign campaign = getCampaignAndValidateOwnership(campaignId, currentMaster.getId());

            // Mappa i dettagli base e i player
            MasterCampaignViewDTO dto = campaignMapper.toMasterViewDTO(campaign);
            
            // --- AGGIUNTA LOGICA CALENDARIO ---
            List<CampaignProposalDTO> proposals = campaign.getProposals().stream()
                    .map(campaignMapper::toProposalDTO)
                    .collect(Collectors.toList());
            
            dto.setScheduledNextSession(campaign.getScheduledNextSession());
            dto.setProposals(proposals);
            // --- FINE AGGIUNTA ---

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

    /**
     * rimuove un giocatore (tramite la sua scheda) da una campagna gestita dal master
     */
    @Transactional // FONDAMENTALE: modifica la collezione players
    public ResponseEntity<?> kickPlayerFromCampaign(Long campaignId, Long characterId) {
        try {
            // ottiene il master loggato
            Master currentMaster = getCurrentMaster();
            
            // ottiene e valida la proprietà della campagna
            Campaign campaign = getCampaignAndValidateOwnership(campaignId, currentMaster.getId());

            // trova la scheda personaggio da rimuovere
            CharacterSheet sheetToKick = characterSheetRepository.findById(characterId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Scheda personaggio non trovata con ID: " + characterId));

            // verifica che la scheda sia in questa campagna
            // (grazie a @Transactional, campaign.getPlayers() viene caricato)
            if (!campaign.getPlayers().contains(sheetToKick)) {
                throw new BadRequestException(
                        "Questa scheda personaggio non fa parte di questa campagna.");
            }

            // rimuove la scheda dalla campagna
            campaign.getPlayers().remove(sheetToKick);
            campaignRepository.save(campaign); // JPA aggiornerà la tabella join 'campaign_players'
            
            String playerName = sheetToKick.getPlayer().getUser().getUsername();
            logger.info("Master {} ha espulso {} (Scheda: {}) dalla campagna {}",
                    currentMaster.getId(), playerName, sheetToKick.getName(), campaign.getTitle());

            return ResponseEntity.ok(new MessageResponse(
                    "Personaggio " + sheetToKick.getName() + " rimosso dalla campagna."));

        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * permette al master loggato di reclamare una campagna orfana usando il codice d'invito corretto
     */
    @Transactional
    public ResponseEntity<?> claimOrphanedCampaign(ClaimCampaignRequest dto) {
        try {
            // ottiene il master loggato
            Master newMaster = getCurrentMaster();

            // trova la campagna tramite il codice d'invito master
            Campaign campaign = campaignRepository.findByInviteMastersCode(dto.getInviteMastersCode())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Codice invito master non valido."));

            // verifica che la campagna è davvero orfana
            // controlla che il timer di 30gg sia attivo
            if (campaign.getMasterBanPendingUntil() == null) {
                throw new BadRequestException(
                        "Questa campagna non è in attesa di un nuovo master.");
            }

            // verifica che il timer è scaduto
            if (campaign.getMasterBanPendingUntil().isBefore(LocalDateTime.now())) {
                throw new BadRequestException(
                        "Il tempo limite per reclamare questa campagna è scaduto.");
            }

            // se ok assegna il nuovo master
            campaign.setMaster(newMaster);
            
            // imposta il timer di cancellazione a null
            campaign.setMasterBanPendingUntil(null);
            
            // salva le modifiche
            campaignRepository.save(campaign);

            logger.info("Campagna ID {} reclamata dal Master ID {}", 
                    campaign.getId(), newMaster.getId());

            return ResponseEntity.ok(new MessageResponse(
                    "Hai preso con successo la gestione della campagna '" + campaign.getTitle() + "'!"));

        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Aggiorna i dettagli (titolo, descrizione) di una campagna.
     */
    @Transactional
    public ResponseEntity<?> updateCampaignDetails(Long campaignId, UpdateCampaignRequest dto) {
        try {
            Master currentMaster = getCurrentMaster();
            Campaign campaign = getCampaignAndValidateOwnership(campaignId, currentMaster.getId());

            // Aggiorna i campi
            campaign.setTitle(dto.getTitle());
            campaign.setDescription(dto.getDescription());
            
            Campaign savedCampaign = campaignRepository.save(campaign);

            // Restituisci la campagna aggiornata (usando il DTO base)
            return ResponseEntity.ok(campaignMapper.toDTO(savedCampaign));

        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Recupera tutte le proposte di sessione per una singola campagna.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<List<CampaignProposalDTO>> getCampaignProposals(Long campaignId) {
        try {
            Master currentMaster = getCurrentMaster();
            Campaign campaign = getCampaignAndValidateOwnership(campaignId, currentMaster.getId());

            // Usa @Transactional per caricare .getVotes() nel mapper
            List<CampaignProposalDTO> proposals = campaign.getProposals().stream()
                    .map(campaignMapper::toProposalDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(proposals);

        } catch (ResourceNotFoundException | UnauthorizedException e) {
            // Se non trova la campagna o non è il proprietario, restituisce lista vuota
            return ResponseEntity.ok(List.of()); 
        }
    }

    
}