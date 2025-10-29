package com.generation.Bugbusters.entity;

import com.generation.Bugbusters.enumeration.Alignment;
import com.generation.Bugbusters.enumeration.Race;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "character_sheet")
@Getter
@Setter
@NoArgsConstructor
public class CharacterSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    // relazioni

    @ManyToOne(fetch = FetchType.LAZY) // molte schede appartengono a un solo player
    @JoinColumn(name = "player_id", nullable = false)
    private Player player; // questo sarà il campo proprietario della relazione

    // campi scheda base

    @Column(nullable = false, length = 20) // nome del personaggio
    private String name;

    @Column(name = "primary_class", nullable = false, length = 20)
    private String primaryClass;

    @Column(name = "primary_level", nullable = false)
    private int primaryLevel = 1; // livello iniziale di default

    @Column(name = "secondary_class", length = 20) // opzionale
    private String secondaryClass;

    @Column(name = "secondary_level")
    private Integer secondaryLevel; // integer per i valori nullabili

    @Enumerated(EnumType.STRING) // salva l'enum come Stringa (es. "LAWFUL_GOOD")
    @Column(name = "alignment")
    private Alignment alignment;

    @Enumerated(EnumType.STRING) // stessa cosa per la razza
    @Column(name = "race")
    private Race race;

    @Column(length = 30) // background del personaggio
    private String background;

    @Column(name = "experience_points")
    private int experiencePoints = 0; // punti esperienza iniziali a 0

    // statistiche
    private Short strength; // short è più che sufficiente tanto va da 0 a 255
    private Short dexterity;
    private Short constitution;
    private Short intelligence;
    private Short wisdom;
    private Short charisma;

    @Column(name = "proficiency_bonus")
    private Short proficiencyBonus; // bonus di competenza

    @Column(name = "max_hit_points")
    private Integer maxHitPoints; // punti ferita massimi

    @Column(name = "current_hit_points")
    private Integer currentHitPoints; // punti ferita attuali

    @Column(name = "temporary_hit_points")
    private int temporaryHitPoints = 0; // punti ferita temporanei

    @Column(name = "armor_class")
    private Short armorClass; // classe armatura

    private Short initiative;
    private Short speed;
    private boolean inspiration = false; // ispirazione sempre false di default

    // competenze nelle abilità
    private boolean acrobaticsSkillProficiency = false;
    private boolean animalHandlingSkillProficiency = false;
    private boolean arcanaSkillProficiency = false;
    private boolean athleticsSkillProficiency = false;
    private boolean deceptionSkillProficiency = false;
    private boolean historySkillProficiency = false;
    private boolean insightSkillProficiency = false;
    private boolean intimidationSkillProficiency = false;
    private boolean investigationSkillProficiency = false;
    private boolean medicineSkillProficiency = false;
    private boolean natureSkillProficiency = false;
    private boolean perceptionSkillProficiency = false;
    private boolean performanceSkillProficiency = false;
    private boolean persuasionSkillProficiency = false;
    private boolean religionSkillProficiency = false;
    private boolean sleightOfHandSkillProficiency = false;
    private boolean stealthSkillProficiency = false;
    private boolean survivalSkillProficiency = false;

    // inventario e descrizione
    private int copperPieces = 0;
    private int silverPieces = 0;
    private int electrumPieces = 0;
    private int goldPieces = 0;
    private int platinumPieces = 0;

    @Column(columnDefinition = "TEXT")
    private String equipment;

    @Column(columnDefinition = "TEXT")
    private String personalityTraits;

    @Column(columnDefinition = "TEXT")
    private String ideals;

    @Column(columnDefinition = "TEXT")
    private String bonds;

    @Column(columnDefinition = "TEXT")
    private String flaws;

    @Column(columnDefinition = "TEXT")
    private String featuresAndTraits;

    @Column(columnDefinition = "TEXT")
    private String proficienciesAndLanguages;
}