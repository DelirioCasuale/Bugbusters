package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.CharacterSheetCreateRequest;
import com.generation.Bugbusters.dto.CharacterSheetDTO;
import com.generation.Bugbusters.dto.CharacterSheetSimpleDTO;
import com.generation.Bugbusters.dto.JoinCampaignRequest;
import com.generation.Bugbusters.dto.JoinedCampaignDTO;
import com.generation.Bugbusters.dto.MessageResponse;
import com.generation.Bugbusters.dto.PlayerSessionProposalDTO;
import com.generation.Bugbusters.entity.Campaign;
import com.generation.Bugbusters.entity.CharacterSheet;
import com.generation.Bugbusters.entity.Player;
import com.generation.Bugbusters.entity.ProposalVote;
import com.generation.Bugbusters.entity.ProposalVoteId;
import com.generation.Bugbusters.entity.SessionProposal;
import com.generation.Bugbusters.exception.ResourceNotFoundException;
import com.generation.Bugbusters.mapper.CharacterSheetMapper;
import com.generation.Bugbusters.repository.CampaignRepository;
import com.generation.Bugbusters.repository.CharacterSheetRepository;
import com.generation.Bugbusters.repository.PlayerRepository;
import com.generation.Bugbusters.repository.ProposalVoteRepository;
import com.generation.Bugbusters.repository.SessionProposalRepository;
import com.generation.Bugbusters.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    private CharacterSheetRepository characterSheetRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private CharacterSheetMapper characterSheetMapper; // iniettiamo il mapper

    @Autowired
    private CampaignRepository campaignRepository; // iniettiamo il repository delle campagne

    @Autowired
    private SessionProposalRepository sessionProposalRepository; // iniettiamo il repository delle proposte di sessione

    @Autowired
    private ProposalVoteRepository proposalVoteRepository; // iniettiamo il repository dei voti delle proposte

    // crea una nuova scheda personaggio per l'utente loggato
    @Transactional
    public CharacterSheetDTO createCharacterSheet(CharacterSheetCreateRequest dto) {
        // ottiene il profilo player dell'utente loggato
        Player currentPlayer = getCurrentPlayer();

        // usa il mapper per convertire il DTO in Entità
        CharacterSheet newSheet = characterSheetMapper.toEntity(dto, currentPlayer);

        // salva l'entità nel database
        CharacterSheet savedSheet = characterSheetRepository.save(newSheet);

        // riconverte l'entità salvata in un DTO e la restituisce
        return characterSheetMapper.toDTO(savedSheet);
    }

    // recupera TUTTE le schede personaggio dell'utente loggato
    @Transactional(readOnly = true) // readOnly = true ottimizza le query in sola lettura
    public List<CharacterSheetDTO> getAllMyCharacterSheets() {
        // ottiene il profilo Player dell'utente loggato
        Player currentPlayer = getCurrentPlayer();

        // cerca nel repository tutte le schede di quel player
        List<CharacterSheet> sheets = characterSheetRepository.findByPlayerId(currentPlayer.getId());

        // converte la lista di Entità in una lista di DTO
        return sheets.stream()
                .map(characterSheetMapper::toDTO)
                .collect(Collectors.toList());
    }

    // metodo helper per ottenere il PROFILO PLAYER dell'utente attualmente loggato
    private Player getCurrentPlayer() {
        // ottiene l'id utente dal contesto di sicurezza
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        // cerca il profilo player corrispondente
        // l'id del player è lo stesso dell'user
        return playerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Profilo Player non trovato per l'utente loggato."));
    }

    // permette al Player loggato di unirsi a una campagna tramite codice invito
    @Transactional
    public ResponseEntity<?> joinCampaign(JoinCampaignRequest dto) {
        
        // ottiene il Player loggato
        Player currentPlayer = getCurrentPlayer();

        // trova la Campagna tramite il codice
        Optional<Campaign> campaignOpt = 
                campaignRepository.findByInvitePlayersCode(dto.getInviteCode());

        if (!campaignOpt.isPresent()) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: Codice invito non valido."), 
                    HttpStatus.NOT_FOUND);
        }
        Campaign campaign = campaignOpt.get();

        // trova la Scheda Personaggio
        Optional<CharacterSheet> sheetOpt = 
                characterSheetRepository.findById(dto.getCharacterSheetId());

        if (!sheetOpt.isPresent()) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: Scheda personaggio non trovata."), 
                    HttpStatus.NOT_FOUND);
        }
        CharacterSheet sheet = sheetOpt.get();

        // controlla che la scheda appartenga al giocatore loggato
        if (!sheet.getPlayer().getId().equals(currentPlayer.getId())) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: Non puoi unirti con una scheda che non è tua."), 
                    HttpStatus.FORBIDDEN);
        }

        // controlla che il giocatore non sia già in questa campagna
        // (Un giocatore non può essere in una campagna con due personaggi diversi)
        boolean alreadyJoined = campaign.getPlayers().stream()
                .anyMatch(s -> s.getPlayer().getId().equals(currentPlayer.getId()));
        
        if (alreadyJoined) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: Fai già parte di questa campagna."), 
                    HttpStatus.BAD_REQUEST);
        }

        // aggiunge la scheda alla campagna
        campaign.getPlayers().add(sheet);
        campaignRepository.save(campaign); // JPA aggiornerà la tabella campaign_players

        return ResponseEntity.ok(
                new MessageResponse("Ti sei unito alla campagna '" + campaign.getTitle() + "'!"));
    }

    // recupera le campagne a cui il giocatore loggato si è unito
    @Transactional(readOnly = true)
    public List<JoinedCampaignDTO> getMyJoinedCampaigns() {
        
        // ottiene il Player loggato
        Player currentPlayer = getCurrentPlayer();
        Long playerId = currentPlayer.getId();

        // trova le campagne usando la nostra query custom
        List<Campaign> campaigns = campaignRepository.findCampaignsByPlayerId(playerId);

        // mappa i risultati (lista di Entità) in DTO
        return campaigns.stream().map(campaign -> {
            
            // trova la scheda specifica che questo giocatore sta usando in QUESTA campagna
            CharacterSheet sheetInUse = campaign.getPlayers().stream()
                    .filter(sheet -> sheet.getPlayer().getId().equals(playerId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Logica corrotta: campagna trovata senza scheda del giocatore"));

            // crea il DTO della scheda
            CharacterSheetSimpleDTO sheetDTO = new CharacterSheetSimpleDTO();
            sheetDTO.setId(sheetInUse.getId());
            sheetDTO.setName(sheetInUse.getName());

            // crea il DTO della campagna
            JoinedCampaignDTO dto = new JoinedCampaignDTO();
            dto.setCampaignId(campaign.getId());
            dto.setCampaignTitle(campaign.getTitle());
            dto.setCharacterUsed(sheetDTO);
            
            return dto;
            
        }).collect(Collectors.toList());
    }

    /**
     * recupera tutte le proposte di sessione attive per tutte le campagne a cui il Player partecipa
     */
    @Transactional(readOnly = true)
    public List<PlayerSessionProposalDTO> getActiveProposals() {
        Player currentPlayer = getCurrentPlayer();
        Long playerId = currentPlayer.getId();

        // trova le campagne del giocatore
        List<Campaign> myCampaigns = campaignRepository.findCampaignsByPlayerId(playerId);

        // colleziona tutte le proposte da tutte le campagne
        List<PlayerSessionProposalDTO> allProposals = new ArrayList<>();

        for (Campaign campaign : myCampaigns) {
            // trova le proposte attive per QUESTA campagna
            List<SessionProposal> activeProposals = sessionProposalRepository
                    .findByCampaignIdAndExpiresOnAfterAndIsConfirmedFalse(
                            campaign.getId(), 
                            LocalDateTime.now());
            
            // mappa le proposte nel DTO
            for (SessionProposal proposal : activeProposals) {
                
                // controlla se il giocatore ha già votato
                // (grazie a @Transactional, proposal.getVotes() viene caricato)
                boolean hasVoted = proposal.getVotes().stream()
                        .anyMatch(vote -> vote.getPlayer().getId().equals(playerId));

                // costruisce il DTO
                allProposals.add(mapProposalToPlayerDTO(proposal, campaign, hasVoted));
            }
        }
        
        return allProposals;
    }

    /**
     * registra il voto del Player loggato per una proposta
     */
    @Transactional
    public ResponseEntity<?> voteForProposal(Long proposalId) {
        Player currentPlayer = getCurrentPlayer();
        Long playerId = currentPlayer.getId();

        // trova la proposta
        SessionProposal proposal = sessionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proposta non trovata con ID: " + proposalId));

        // verifica se è ancora attiva
        if (proposal.isConfirmed() || proposal.getExpiresOn().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: La votazione per questa proposta è chiusa."), 
                    HttpStatus.BAD_REQUEST);
        }

        // verifica se il player è in questa campagna
        Campaign campaign = proposal.getCampaign();
        boolean isMember = campaign.getPlayers().stream()
                .anyMatch(sheet -> sheet.getPlayer().getId().equals(playerId));
        
        if (!isMember) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: Non fai parte della campagna per cui stai votando."), 
                    HttpStatus.FORBIDDEN);
        }

        // verifica se il player ha già votato
        ProposalVoteId voteId = new ProposalVoteId(proposalId, playerId);
        if (proposalVoteRepository.existsById(voteId)) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: Hai già votato per questa proposta."), 
                    HttpStatus.BAD_REQUEST);
        }

        // se tutto ok registra il voto
        ProposalVote newVote = new ProposalVote();
        newVote.setId(voteId);
        newVote.setPlayer(currentPlayer);
        newVote.setProposal(proposal);
        
        proposalVoteRepository.save(newVote);

        return ResponseEntity.ok(new MessageResponse("Voto registrato con successo!"));
    }


    /**
     * metodo helper per mappare un'entità proposal nel dto
     */
    private PlayerSessionProposalDTO mapProposalToPlayerDTO(
            SessionProposal proposal, Campaign campaign, boolean hasVoted) {
        
        PlayerSessionProposalDTO dto = new PlayerSessionProposalDTO();
        dto.setProposalId(proposal.getId());
        dto.setCampaignId(campaign.getId());
        dto.setCampaignTitle(campaign.getTitle());
        dto.setProposedDate(proposal.getProposedDate());
        dto.setExpiresOn(proposal.getExpiresOn());
        dto.setConfirmed(proposal.isConfirmed());
        dto.setHasVoted(hasVoted);
        return dto;
    }
}