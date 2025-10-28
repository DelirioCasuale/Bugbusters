package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.CampaignCreateRequest;
import com.generation.Bugbusters.dto.CampaignDTO;
import com.generation.Bugbusters.entity.Campaign;
import com.generation.Bugbusters.entity.Master;
import com.generation.Bugbusters.mapper.CampaignMapper;
import com.generation.Bugbusters.repository.CampaignRepository;
import com.generation.Bugbusters.repository.MasterRepository;
import com.generation.Bugbusters.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}