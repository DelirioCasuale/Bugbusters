package com.generation.Bugbusters.mapper;

import com.generation.Bugbusters.dto.CampaignCreateRequest;
import com.generation.Bugbusters.dto.CampaignDTO;
import com.generation.Bugbusters.dto.CampaignPlayerDTO;
import com.generation.Bugbusters.dto.CampaignProposalDTO;
import com.generation.Bugbusters.dto.MasterCampaignViewDTO;
import com.generation.Bugbusters.entity.Campaign;
import com.generation.Bugbusters.entity.CharacterSheet;
import com.generation.Bugbusters.entity.Master;
import com.generation.Bugbusters.entity.SessionProposal;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CampaignMapper {

    // converte un DTO di creazione in un'Entità pronta per essere salvata
    public Campaign toEntity(CampaignCreateRequest dto, Master owner) {
        Campaign entity = new Campaign();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setMaster(owner); // collega il master proprietario
        
        // genera i codici d'invito unici 
        entity.setInvitePlayersCode(generateUniqueCode(8));
        entity.setInviteMastersCode(generateUniqueCode(10));
        
        // (startDate sarà null finché il master non la imposta)
        
        return entity;
    }

    // converte un'Entità in un DTO sicuro da esporre
    public CampaignDTO toDTO(Campaign entity) {
        CampaignDTO dto = new CampaignDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartDate(entity.getStartDate());
        dto.setInvitePlayersCode(entity.getInvitePlayersCode());
        dto.setInviteMastersCode(entity.getInviteMastersCode());
        
        return dto;
    }

    // helper per generare codici
    // usiamo UUID per garantire unicità e semplicità
    private String generateUniqueCode(int length) {
        return UUID.randomUUID().toString()
                .replace("-", "") // rimuove i trattini
                .substring(0, length) // prende i primi 'length' caratteri
                .toUpperCase(); // li rende maiuscoli per leggibilità
    }

    // Converte un'Entità Campaign in una vista dettagliata per il Master, includendo i giocatori.
    public MasterCampaignViewDTO toMasterViewDTO(Campaign entity) {
        MasterCampaignViewDTO dto = new MasterCampaignViewDTO();
        
        // Mappa i campi base
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartDate(entity.getStartDate());
        dto.setInvitePlayersCode(entity.getInvitePlayersCode());
        dto.setInviteMastersCode(entity.getInviteMastersCode());

        // Mappa la lista di giocatori (Set<CharacterSheet>)
        List<CampaignPlayerDTO> playerDTOs = entity.getPlayers().stream()
                .map(this::mapSheetToCampaignPlayerDTO) // Metodo helper (DRY)
                .collect(Collectors.toList());
        
        dto.setPlayers(playerDTOs);
        
        return dto;
    }

    /*
     * Metodo helper (DRY) per mappare una CharacterSheet nel DTO che serve alla vista del Master
     * N.B: Questo metodo richiede che le entità 'player' e 'user' siano state caricate (il service lo farà con @Transactional)
     * --- MODIFICA: 'public' per usarlo in PlayerService ---
     */
    public CampaignPlayerDTO mapSheetToCampaignPlayerDTO(CharacterSheet sheet) {
        CampaignPlayerDTO dto = new CampaignPlayerDTO();
        dto.setPlayerId(sheet.getPlayer().getId());
        dto.setUsername(sheet.getPlayer().getUser().getUsername());
        dto.setCharacterId(sheet.getId());
        dto.setCharacterName(sheet.getName());
        dto.setCharacterClass(sheet.getPrimaryClass());
        dto.setCharacterLevel(sheet.getPrimaryLevel());
        return dto;
    }

    /**
     * Converte un'entità SessionProposal in un DTO per la vista Master.
     * (Richiede @Transactional per accedere a .getVotes())
     */
    public CampaignProposalDTO toProposalDTO(SessionProposal proposal) {
        CampaignProposalDTO dto = new CampaignProposalDTO();
        dto.setId(proposal.getId());
        dto.setProposedDate(proposal.getProposedDate());
        dto.setExpiresOn(proposal.getExpiresOn());
        dto.setConfirmed(proposal.isConfirmed());
        dto.setVoteCount(proposal.getVotes().size()); // Mostra al master quanti hanno votato
        return dto;
    }
}