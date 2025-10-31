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

    /**
     * Aggiorna un'entità CharacterSheet esistente con i dati di un DTO.
     * Non aggiorna l'ID o il player.
     */
    public void updateEntityFromDTO(CharacterSheet entity, CharacterSheetDTO dto) {
        
        // Info Base
        entity.setName(dto.getName());
        entity.setPrimaryClass(dto.getPrimaryClass());
        entity.setPrimaryLevel(dto.getPrimaryLevel());
        entity.setSecondaryClass(dto.getSecondaryClass());
        entity.setSecondaryLevel(dto.getSecondaryLevel());
        entity.setAlignment(dto.getAlignment());
        entity.setRace(dto.getRace());
        entity.setBackground(dto.getBackground());
        entity.setExperiencePoints(dto.getExperiencePoints());

        // Statistiche
        entity.setStrength(dto.getStrength());
        entity.setDexterity(dto.getDexterity());
        entity.setConstitution(dto.getConstitution());
        entity.setIntelligence(dto.getIntelligence());
        entity.setWisdom(dto.getWisdom());
        entity.setCharisma(dto.getCharisma());

        // Combat Stats
        entity.setProficiencyBonus(dto.getProficiencyBonus());
        entity.setMaxHitPoints(dto.getMaxHitPoints());
        entity.setCurrentHitPoints(dto.getCurrentHitPoints());
        entity.setTemporaryHitPoints(dto.getTemporaryHitPoints());
        entity.setArmorClass(dto.getArmorClass());
        entity.setInitiative(dto.getInitiative());
        entity.setSpeed(dto.getSpeed());
        entity.setInspiration(dto.isInspiration());

        // Skills (Proficiency)
        entity.setAcrobaticsSkillProficiency(dto.isAcrobaticsSkillProficiency());
        entity.setAnimalHandlingSkillProficiency(dto.isAnimalHandlingSkillProficiency());
        entity.setArcanaSkillProficiency(dto.isArcanaSkillProficiency());
        entity.setAthleticsSkillProficiency(dto.isAthleticsSkillProficiency());
        entity.setDeceptionSkillProficiency(dto.isDeceptionSkillProficiency());
        entity.setHistorySkillProficiency(dto.isHistorySkillProficiency());
        entity.setInsightSkillProficiency(dto.isInsightSkillProficiency());
        entity.setIntimidationSkillProficiency(dto.isIntimidationSkillProficiency());
        entity.setInvestigationSkillProficiency(dto.isInvestigationSkillProficiency());
        entity.setMedicineSkillProficiency(dto.isMedicineSkillProficiency());
        entity.setNatureSkillProficiency(dto.isNatureSkillProficiency());
        entity.setPerceptionSkillProficiency(dto.isPerceptionSkillProficiency());
        entity.setPerformanceSkillProficiency(dto.isPerformanceSkillProficiency());
        entity.setPersuasionSkillProficiency(dto.isPersuasionSkillProficiency());
        entity.setReligionSkillProficiency(dto.isReligionSkillProficiency());
        entity.setSleightOfHandSkillProficiency(dto.isSleightOfHandSkillProficiency());
        entity.setStealthSkillProficiency(dto.isStealthSkillProficiency());
        entity.setSurvivalSkillProficiency(dto.isSurvivalSkillProficiency());

        // Inventario e Valuta
        entity.setCopperPieces(dto.getCopperPieces());
        entity.setSilverPieces(dto.getSilverPieces());
        entity.setElectrumPieces(dto.getElectrumPieces());
        entity.setGoldPieces(dto.getGoldPieces());
        entity.setPlatinumPieces(dto.getPlatinumPieces());
        entity.setEquipment(dto.getEquipment());

        // Background e Roleplay (TEXT)
        entity.setPersonalityTraits(dto.getPersonalityTraits());
        entity.setIdeals(dto.getIdeals());
        entity.setBonds(dto.getBonds());
        entity.setFlaws(dto.getFlaws());
        entity.setFeaturesAndTraits(dto.getFeaturesAndTraits());
        entity.setProficienciesAndLanguages(dto.getProficienciesAndLanguages());
    }
}