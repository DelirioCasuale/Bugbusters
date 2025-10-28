package com.generation.Bugbusters.mapper;

import com.generation.Bugbusters.dto.CampaignCreateRequest;
import com.generation.Bugbusters.dto.CampaignDTO;
import com.generation.Bugbusters.entity.Campaign;
import com.generation.Bugbusters.entity.Master;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
}