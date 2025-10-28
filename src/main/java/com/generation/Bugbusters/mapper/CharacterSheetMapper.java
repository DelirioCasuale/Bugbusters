package com.generation.Bugbusters.mapper;

import com.generation.Bugbusters.dto.CharacterSheetCreateRequest;
import com.generation.Bugbusters.dto.CharacterSheetDTO;
import com.generation.Bugbusters.entity.CharacterSheet;
import com.generation.Bugbusters.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class CharacterSheetMapper {

    
    // converte un DTO di creazione in un'Entità pronta per essere salvata
    // imposta tutti i valori di default come da logica D&D
    public CharacterSheet toEntity(CharacterSheetCreateRequest dto, Player owner) {
        CharacterSheet entity = new CharacterSheet();

        // campi del DTO
        entity.setName(dto.getName());
        entity.setPrimaryClass(dto.getPrimaryClass());
        entity.setRace(dto.getRace());
        
        // proprietario della scheda
        entity.setPlayer(owner); 
        
        // imposta TUTTI gli altri campi a un valore di default
        
        // info base
        entity.setPrimaryLevel(1);
        entity.setSecondaryClass(null);
        entity.setSecondaryLevel(null);
        entity.setAlignment(null); // il giocatore lo sceglierà dopo
        entity.setBackground(null); // il giocatore lo sceglierà dopo
        entity.setExperiencePoints(0);

        // statistiche (Default 10)
        entity.setStrength((short) 10);
        entity.setDexterity((short) 10);
        entity.setConstitution((short) 10);
        entity.setIntelligence((short) 10);
        entity.setWisdom((short) 10);
        entity.setCharisma((short) 10);
        
        // statistiche combattive
        // N.B: andrebbero ricalcolati in logica di business, cioè in PlayerService
        entity.setProficiencyBonus((short) 2); 
        entity.setMaxHitPoints(10); // placeholder (es. 10 + mod CON)
        entity.setCurrentHitPoints(10); 
        entity.setTemporaryHitPoints(0);
        entity.setArmorClass((short) 10); // placeholder (es. 10 + mod DEX)
        entity.setInitiative((short) 0); // placeholder (solo mod DEX)
        entity.setSpeed((short) 30); // placeholder (dipende dalla razza)
        entity.setInspiration(false);

        // skill (tutte non proficienti di default)
        entity.setAcrobaticsSkillProficiency(false);
        entity.setAnimalHandlingSkillProficiency(false);
        entity.setArcanaSkillProficiency(false);
        entity.setAthleticsSkillProficiency(false);
        entity.setDeceptionSkillProficiency(false);
        entity.setHistorySkillProficiency(false);
        entity.setInsightSkillProficiency(false);
        entity.setIntimidationSkillProficiency(false);
        entity.setInvestigationSkillProficiency(false);
        entity.setMedicineSkillProficiency(false);
        entity.setNatureSkillProficiency(false);
        entity.setPerceptionSkillProficiency(false);
        entity.setPerformanceSkillProficiency(false);
        entity.setPersuasionSkillProficiency(false);
        entity.setReligionSkillProficiency(false);
        entity.setSleightOfHandSkillProficiency(false);
        entity.setStealthSkillProficiency(false);
        entity.setSurvivalSkillProficiency(false);

        // inventario (tutto a 0)
        entity.setCopperPieces(0);
        entity.setSilverPieces(0);
        entity.setElectrumPieces(0);
        entity.setGoldPieces(0);
        entity.setPlatinumPieces(0);
        
        // campi di testo (vuoti/null di default)
        entity.setEquipment(null);
        entity.setPersonalityTraits(null);
        entity.setIdeals(null);
        entity.setBonds(null);
        entity.setFlaws(null);
        entity.setFeaturesAndTraits(null);
        entity.setProficienciesAndLanguages(null);
        
        return entity;
    }

    // converte un'Entità completa in un DTO sicuro da esporre
    // questo è un mapping 1:1
    public CharacterSheetDTO toDTO(CharacterSheet entity) {
        CharacterSheetDTO dto = new CharacterSheetDTO();
        
        dto.setId(entity.getId());
        
        // info base
        dto.setName(entity.getName());
        dto.setPrimaryClass(entity.getPrimaryClass());
        dto.setPrimaryLevel(entity.getPrimaryLevel());
        dto.setSecondaryClass(entity.getSecondaryClass());
        dto.setSecondaryLevel(entity.getSecondaryLevel());
        dto.setAlignment(entity.getAlignment());
        dto.setRace(entity.getRace());
        dto.setBackground(entity.getBackground());
        dto.setExperiencePoints(entity.getExperiencePoints());

        // statistiche
        dto.setStrength(entity.getStrength());
        dto.setDexterity(entity.getDexterity());
        dto.setConstitution(entity.getConstitution());
        dto.setIntelligence(entity.getIntelligence());
        dto.setWisdom(entity.getWisdom());
        dto.setCharisma(entity.getCharisma());

        // statistiche di combattimento
        dto.setProficiencyBonus(entity.getProficiencyBonus());
        dto.setMaxHitPoints(entity.getMaxHitPoints());
        dto.setCurrentHitPoints(entity.getCurrentHitPoints());
        dto.setTemporaryHitPoints(entity.getTemporaryHitPoints());
        dto.setArmorClass(entity.getArmorClass());
        dto.setInitiative(entity.getInitiative());
        dto.setSpeed(entity.getSpeed());
        dto.setInspiration(entity.isInspiration()); 

        // skills
        dto.setAcrobaticsSkillProficiency(entity.isAcrobaticsSkillProficiency());
        dto.setAnimalHandlingSkillProficiency(entity.isAnimalHandlingSkillProficiency());
        dto.setArcanaSkillProficiency(entity.isArcanaSkillProficiency());
        dto.setAthleticsSkillProficiency(entity.isAthleticsSkillProficiency());
        dto.setDeceptionSkillProficiency(entity.isDeceptionSkillProficiency());
        dto.setHistorySkillProficiency(entity.isHistorySkillProficiency());
        dto.setInsightSkillProficiency(entity.isInsightSkillProficiency());
        dto.setIntimidationSkillProficiency(entity.isIntimidationSkillProficiency());
        dto.setInvestigationSkillProficiency(entity.isInvestigationSkillProficiency());
        dto.setMedicineSkillProficiency(entity.isMedicineSkillProficiency());
        dto.setNatureSkillProficiency(entity.isNatureSkillProficiency());
        dto.setPerceptionSkillProficiency(entity.isPerceptionSkillProficiency());
        dto.setPerformanceSkillProficiency(entity.isPerformanceSkillProficiency());
        dto.setPersuasionSkillProficiency(entity.isPersuasionSkillProficiency());
        dto.setReligionSkillProficiency(entity.isReligionSkillProficiency());
        dto.setSleightOfHandSkillProficiency(entity.isSleightOfHandSkillProficiency());
        dto.setStealthSkillProficiency(entity.isStealthSkillProficiency());
        dto.setSurvivalSkillProficiency(entity.isSurvivalSkillProficiency());

        // inventario e valuta
        dto.setCopperPieces(entity.getCopperPieces());
        dto.setSilverPieces(entity.getSilverPieces());
        dto.setElectrumPieces(entity.getElectrumPieces());
        dto.setGoldPieces(entity.getGoldPieces());
        dto.setPlatinumPieces(entity.getPlatinumPieces());
        dto.setEquipment(entity.getEquipment());

        // background e roleplay
        dto.setPersonalityTraits(entity.getPersonalityTraits());
        dto.setIdeals(entity.getIdeals());
        dto.setBonds(entity.getBonds());
        dto.setFlaws(entity.getFlaws());
        dto.setFeaturesAndTraits(entity.getFeaturesAndTraits());
        dto.setProficienciesAndLanguages(entity.getProficienciesAndLanguages());
        
        return dto;
    }
}