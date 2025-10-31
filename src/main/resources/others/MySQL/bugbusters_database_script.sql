DROP DATABASE IF EXISTS tavern_portal;

CREATE DATABASE tavern_portal;

USE tavern_portal;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- NUOVA COLONNA --
    profile_image_url VARCHAR(255) DEFAULT NULL,
    
    is_banned BOOLEAN DEFAULT FALSE,
    deletion_scheduled_on TIMESTAMP DEFAULT NULL 
);

CREATE TABLE roles(
	id INT AUTO_INCREMENT PRIMARY KEY,
	role_name VARCHAR(50) NOT NULL UNIQUE
);

-- Ho modificato ROLE_GUEST in ROLE_USER per chiarezza --
INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN'),('ROLE_USER');


CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);


CREATE TABLE players (
    user_id BIGINT NOT NULL PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE character_sheet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    player_id BIGINT NOT NULL, 
    FOREIGN KEY (player_id) REFERENCES players (user_id) ON DELETE CASCADE,
    name VARCHAR(20) NOT NULL,
    primary_class VARCHAR(20) NOT NULL,
    primary_level INT NOT NULL DEFAULT 1,
    secondary_class VARCHAR(20),
    secondary_level INT DEFAULT NULL,
    alignment ENUM(
        'LAWFUL_GOOD',
        'NEUTRAL_GOOD',
        'CHAOTIC_GOOD',
        'LAWFUL_NEUTRAL',
        'TRUE_NEUTRAL',
        'CHAOTIC_NEUTRAL',
        'LAWFUL_EVIL',
        'NEUTRAL_EVIL',
        'CHAOTIC_EVIL'
    ),
    race ENUM(
        'HUMAN',
		'ELF', 
		'DWARF',
		'HALFLING',
		'ORC',
		'GNOME',
		'TIEFLING',
		'DRAGONBORN',
		'HALF_ELF',
		'HALF_ORC'
    ),
    background VARCHAR(30),
    experience_points INT DEFAULT 0,
    strength SMALLINT, -- MODIFICATO da TINYINT UNSIGNED
    dexterity SMALLINT, -- MODIFICATO da TINYINT UNSIGNED
    constitution SMALLINT, -- MODIFICATO da TINYINT UNSIGNED
    intelligence SMALLINT, -- MODIFICATO da TINYINT UNSIGNED
    wisdom SMALLINT, -- MODIFICATO da TINYINT UNSIGNED
    charisma SMALLINT, -- MODIFICATO da TINYINT UNSIGNED
    proficiency_bonus SMALLINT, -- MODIFICATO da TINYINT
    max_hit_points INT,
    current_hit_points INT,
    temporary_hit_points INT DEFAULT 0,
    armor_class SMALLINT, -- MODIFICATO da TINYINT
    initiative SMALLINT, -- MODIFICATO da TINYINT
    speed SMALLINT, -- MODIFICATO da TINYINT
    inspiration BOOLEAN DEFAULT FALSE,
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

CREATE TABLE masters (
    user_id BIGINT NOT NULL PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE campaigns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    master_id BIGINT, -- MODIFICATO: Rende il master_id NULLABILE per la logica di ban
    title VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE,
    scheduled_next_session DATE,
    FOREIGN KEY (master_id) REFERENCES masters (user_id) ON DELETE SET NULL, -- ON DELETE SET NULL se il master viene eliminato
    invite_players_code VARCHAR(10) UNIQUE, 
    invite_masters_code VARCHAR(10) UNIQUE,

    -- COLONNA AGGIUNTA PER IL BAN DEL MASTER --
    master_ban_pending_until TIMESTAMP DEFAULT NULL -- Scadenza di 30 giorni per trovare un nuovo master
);

CREATE TABLE campaign_players (
    campaign_id BIGINT NOT NULL,
    character_id BIGINT NOT NULL,
    PRIMARY KEY (campaign_id, character_id),
    FOREIGN KEY (campaign_id) REFERENCES campaigns (id) ON DELETE CASCADE,
    FOREIGN KEY (character_id) REFERENCES character_sheet (id) ON DELETE CASCADE
);

CREATE TABLE campaign_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    campaign_id BIGINT NOT NULL,
    session_date DATE NOT NULL,
    summary TEXT,
    FOREIGN KEY (campaign_id) REFERENCES campaigns (id) ON DELETE CASCADE
);


-- TABELLE AGGIUNTE PER LA LOGICA DI VOTAZIONE SESSIONI --

CREATE TABLE session_proposals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    campaign_id BIGINT NOT NULL,
    proposed_date TIMESTAMP NOT NULL,
    expires_on TIMESTAMP NOT NULL, -- Per la logica della scadenza di 48h
    is_confirmed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (campaign_id) REFERENCES campaigns (id) ON DELETE CASCADE
);

CREATE TABLE proposal_votes (
    proposal_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL, -- ID utente del giocatore che vota
    PRIMARY KEY (proposal_id, player_id),
    FOREIGN KEY (proposal_id) REFERENCES session_proposals (id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players (user_id) ON DELETE CASCADE
);