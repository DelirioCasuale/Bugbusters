package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.CharacterSheetCreateRequest;
import com.generation.Bugbusters.dto.CharacterSheetDTO;
import com.generation.Bugbusters.dto.CharacterSheetSimpleDTO;
import com.generation.Bugbusters.dto.JoinCampaignRequest;
import com.generation.Bugbusters.dto.JoinedCampaignDTO;
import com.generation.Bugbusters.dto.MessageResponse;
import com.generation.Bugbusters.entity.Campaign;
import com.generation.Bugbusters.entity.CharacterSheet;
import com.generation.Bugbusters.entity.Player;
import com.generation.Bugbusters.mapper.CharacterSheetMapper;
import com.generation.Bugbusters.repository.CampaignRepository;
import com.generation.Bugbusters.repository.CharacterSheetRepository;
import com.generation.Bugbusters.repository.PlayerRepository;
import com.generation.Bugbusters.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}