package com.generation.Bugbusters.dto;

import com.generation.Bugbusters.enumeration.Alignment;
import com.generation.Bugbusters.enumeration.Race;
import lombok.Data;

// DTO completo per la VISUALIZZAZIONE di una scheda personaggio
// rispecchia l'entità, ma è un oggetto "sicuro" da inviare come JSON nella risposta dell'API
@Data
public class CharacterSheetDTO {
    
    private Long id;
    
    // info base
    private String name;
    private String primaryClass;
    private int primaryLevel;
    private String secondaryClass;
    private Integer secondaryLevel;
    private Alignment alignment;
    private Race race;
    private String background;
    private int experiencePoints;
    
    // statistiche
    private Short strength;
    private Short dexterity;
    private Short constitution;
    private Short intelligence;
    private Short wisdom;
    private Short charisma;
    
    // statistiche di combattimento
    private Short proficiencyBonus;
    private Integer maxHitPoints;
    private Integer currentHitPoints;
    private int temporaryHitPoints;
    private Short armorClass;
    private Short initiative;
    private Short speed;
    private boolean inspiration;
    
    // competenze nelle abilità (skill proficiencies)
    private boolean acrobaticsSkillProficiency;
    private boolean animalHandlingSkillProficiency;
    private boolean arcanaSkillProficiency;
    private boolean athleticsSkillProficiency;
    private boolean deceptionSkillProficiency;
    private boolean historySkillProficiency;
    private boolean insightSkillProficiency;
    private boolean intimidationSkillProficiency;
    private boolean investigationSkillProficiency;
    private boolean medicineSkillProficiency;
    private boolean natureSkillProficiency;
    private boolean perceptionSkillProficiency;
    private boolean performanceSkillProficiency;
    private boolean persuasionSkillProficiency;
    private boolean religionSkillProficiency;
    private boolean sleightOfHandSkillProficiency;
    private boolean stealthSkillProficiency;
    private boolean survivalSkillProficiency;
    
    // inventario e monete
    private int copperPieces;
    private int silverPieces;
    private int electrumPieces;
    private int goldPieces;
    private int platinumPieces;
    private String equipment; // TEXT
    
    // background e roleplay
    private String personalityTraits; // TEXT
    private String ideals; // TEXT
    private String bonds; // TEXT
    private String flaws; // TEXT
    private String featuresAndTraits; // TEXT
    private String proficienciesAndLanguages; // TEXT
}