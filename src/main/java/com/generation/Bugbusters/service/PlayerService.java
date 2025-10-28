package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.CharacterSheetCreateRequest;
import com.generation.Bugbusters.dto.CharacterSheetDTO;
import com.generation.Bugbusters.entity.CharacterSheet;
import com.generation.Bugbusters.entity.Player;
import com.generation.Bugbusters.mapper.CharacterSheetMapper;
import com.generation.Bugbusters.repository.CharacterSheetRepository;
import com.generation.Bugbusters.repository.PlayerRepository;
import com.generation.Bugbusters.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    private CharacterSheetRepository characterSheetRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private CharacterSheetMapper characterSheetMapper; // iniettiamo il mapper

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
}