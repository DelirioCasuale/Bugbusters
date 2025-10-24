DROP DATABASE IF EXISTS tavern_portal;

CREATE DATABASE tavern_portal;

USE tavern_portal;

CREATE TABLE admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE character_sheet (
    -- aggiungere una funzione di stampa/salvataggio in PDF?
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_id INT NOT NULL, -- verrà anche usato per visualizzare il nome giocatore sulla scheda personaggio 
    FOREIGN KEY (player_id) REFERENCES players (id) ON DELETE CASCADE,
    name VARCHAR(20) NOT NULL,
    primary_class VARCHAR(20) NOT NULL,
    primary_level INT NOT NULL DEFAULT 1,
    secondary_class VARCHAR(20),
    secondary_level INT DEFAULT NULL,
    alignment ENUM(
        'Lawful Good',
        'Neutral Good',
        'Chaotic Good',
        'Lawful Neutral',
        'True Neutral',
        'Chaotic Neutral',
        'Lawful Evil',
        'Neutral Evil',
        'Chaotic Evil'
    ),
    race ENUM(
        'Human',
        'Elf',
        'Dwarf',
        'Halfling',
        'Orc',
        'Gnome',
        'Tiefling',
        'Dragonborn',
        'Half-Elf',
        'Half-Orc'
    ),
    background VARCHAR(30),
    experience_points INT DEFAULT 0,
    strength TINYINT UNSIGNED,
    dexterity TINYINT UNSIGNED,
    constitution TINYINT UNSIGNED,
    intelligence TINYINT UNSIGNED,
    wisdom TINYINT UNSIGNED,
    charisma TINYINT UNSIGNED,
    proficiency_bonus TINYINT,
    max_hit_points INT,
    current_hit_points INT,
    temporary_hit_points INT DEFAULT 0,
    -- hit_dices derivato da tipo di classe e livello delle classi (es guerriero liello 4 = 4d10), si usa per recuperare punti ferita col riposo breve
    armor_class TINYINT,
    initiative TINYINT,
    speed TINYINT,
    -- proficiency_bonus TINYINT derivato da livello totale del personaggio
    -- modifier_strength TINYINT, i modificatori si calcolano come (caratteristica-10)/2 arrotondato per difetto
    -- modifier_dexterity TINYINT,
    -- modifier_constitution TINYINT,
    -- modifier_intelligence TINYINT,
    -- modifier_wisdom TINYINT,
    -- modifier_charisma TINYINT,
    inspiration BOOLEAN DEFAULT FALSE,
    -- saving_throw_strength BOOLEAN DEFAULT FALSE, derivati dalla classe primaria
    -- saving_throw_dexterity BOOLEAN DEFAULT FALSE,
    -- saving_throw_constitution BOOLEAN DEFAULT FALSE,
    -- saving_throw_intelligence BOOLEAN DEFAULT FALSE,
    -- saving_throw_wisdom BOOLEAN DEFAULT FALSE,
    -- saving_throw_charisma BOOLEAN DEFAULT FALSE
    acrobatics_skill_proficiency BOOLEAN DEFAULT FALSE,
    animal_handling_skill_proficiency BOOLEAN DEFAULT FALSE,
    arcana_skill_proficiency BOOLEAN DEFAULT FALSE,
    athletics_skill_proficiency BOOLEAN DEFAULT FALSE,
    deception_skill_proficiency BOOLEAN DEFAULT FALSE,
    history_skill_proficiency BOOLEAN DEFAULT FALSE,
    insight_skill_proficiency BOOLEAN DEFAULT FALSE,
    intimidation_skill_proficiency BOOLEAN DEFAULT FALSE,
    investigation_skill_proficiency BOOLEAN DEFAULT FALSE,
    medicine_skill_proficiency BOOLEAN DEFAULT FALSE,
    nature_skill_proficiency BOOLEAN DEFAULT FALSE,
    perception_skill_proficiency BOOLEAN DEFAULT FALSE,
    performance_skill_proficiency BOOLEAN DEFAULT FALSE,
    persuasion_skill_proficiency BOOLEAN DEFAULT FALSE,
    religion_skill_proficiency BOOLEAN DEFAULT FALSE,
    sleight_of_hand_skill_proficiency BOOLEAN DEFAULT FALSE,
    stealth_skill_proficiency BOOLEAN DEFAULT FALSE,
    survival_skill_proficiency BOOLEAN DEFAULT FALSE,
    -- weapons and speellcasting gestita in altra tabella many to many
    copper_pieces INT DEFAULT 0,
    silver_pieces INT DEFAULT 0,
    electrum_pieces INT DEFAULT 0,
    gold_pieces INT DEFAULT 0,
    platinum_pieces INT DEFAULT 0,
    equipment TEXT,
    personality_traits TEXT,
    ideals TEXT,
    bonds TEXT,
    flaws TEXT,
    features_and_traits TEXT,
    proficiencies_and_languages TEXT
);

CREATE TABLE spells_n_weapons (
    -- tabella many to many per gestire armi e incantesimi di un personaggio
    character_sheet_id INT NOT NULL,
    attack_name VARCHAR(50) NOT NULL,
    is_weapon BOOLEAN NOT NULL, -- TRUE se è un'arma, FALSE se è un incantesimo
    modifier ENUM('strength', 'dexterity', 'none'),
    dice_count TINYINT DEFAULT 1,
    damage_dice ENUM(
        'd4',
        'd6',
        'd8',
        'd10',
        'd12'
    ),
    FOREIGN KEY (character_sheet_id) REFERENCES character_sheet (id) ON DELETE CASCADE,
    PRIMARY KEY (
        character_sheet_id,
        attack_name
    )
);

-- CREATE TABLE character_spells_n_weapons (
--     character_sheet_id INT NOT NULL,
--     is_weapon BOOLEAN NOT NULL, -- TRUE se è un'arma, FALSE se è un incantesimo
--     spell_id INT,
--     weapon_id INT,
--     FOREIGN KEY (character_sheet_id) REFERENCES character_sheet (id) ON DELETE CASCADE,
--     FOREIGN KEY (spell_id) REFERENCES spells (id) ON DELETE CASCADE,
--     FOREIGN KEY (weapon_id) REFERENCES weapons (id) ON DELETE CASCADE,
--     PRIMARY KEY (character_sheet_id, spell_id, weapon_id),
--     CHECK (
--         (spell_id IS NOT NULL AND weapon_id IS NULL) OR
--         (spell_id IS NULL AND weapon_id IS NOT NULL)
--     )
-- );

-- CREATE TABLE weapons (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     character_sheet_id INT NOT NULL,
--     name VARCHAR(50) NOT NULL,
--     weapon_type VARCHAR(30),
--     dice_count TINYINT DEFAULT 1,
--     damage_dice ENUM(
--         'd4',
--         'd6',
--         'd8',
--         'd10',
--         'd12'
--     ),
--     damage_type VARCHAR(30),
--     -- attack_bonus  il modificatore è derivato dalla statistica di riferimento (forza o destrezza) + proficiency se competente; la proficiency è determinata da classi o razze
-- );

-- CREATE TABLE spells (
--     id INT AUTO_INCREMENT PRIMARY KEY,
--     character_sheet_id INT NOT NULL,
--     name VARCHAR(50) NOT NULL,
--     spell_level TINYINT,
--     school_of_magic VARCHAR(30),
--     casting_time VARCHAR(20),
--     reach VARCHAR(20),
--     components VARCHAR(50),
--     duration VARCHAR(20),
--     spell_description TEXT,
--     higher_level_description TEXT
-- );

CREATE TABLE masters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE campaigns (
    id INT AUTO_INCREMENT PRIMARY KEY,
    master_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE,
    -- last_session_date DATE, derivabile da campaign_sessions con join id=campaign_id e max(session_date)
    scheduled_next_session DATE,
    FOREIGN KEY (master_id) REFERENCES masters (id),
    -- number_of_sessions INT DEFAULT 0
    invite_players_code VARCHAR(10) UNIQUE, -- link visibile al master per invitare nuovi giocatori nella campagna
    invite_masters_code VARCHAR(10) UNIQUE -- questo link è visibile al master se presente per trasferire il controllo da un master all'altro o è visibile ai giocatori se il master non è più presente nella campagna
);

CREATE TABLE campaign_players (
    campaign_id INT NOT NULL,
    player_id INT NOT NULL,
    PRIMARY KEY (campaign_id, player_id),
    FOREIGN KEY (campaign_id) REFERENCES campaigns (id),
    FOREIGN KEY (player_id) REFERENCES players (id)
);

CREATE TABLE campaign_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    campaign_id INT NOT NULL,
    session_date DATE NOT NULL,
    summary TEXT,
    FOREIGN KEY (campaign_id) REFERENCES campaigns (id)
);

DELIMITER $$

CREATE TRIGGER after_master_delete 
AFTER DELETE ON masters
FOR EACH ROW
BEGIN
    -- Declare a variable to store the player_id
    DECLARE new_player_id INT;

    -- Get the player_id from campaign_players for the deleted master_id
    SELECT player_id
    INTO new_player_id
    FROM campaign_players
    WHERE campaign_id IN (SELECT id FROM campaigns WHERE master_id = OLD.id)
    LIMIT 1;

    -- Proceed if a valid player_id is found
    IF new_player_id IS NOT NULL THEN
        -- Insert a new row into the masters table using the player_id
        INSERT INTO masters (user_id)
        VALUES (new_player_id);

        -- Update the campaigns table, setting the new master_id to the newly inserted master
        UPDATE campaigns 
        SET master_id = (SELECT MAX(id) FROM masters WHERE user_id = new_player_id)
        WHERE master_id = OLD.id;
    END IF;
END$$

DELIMITER ;