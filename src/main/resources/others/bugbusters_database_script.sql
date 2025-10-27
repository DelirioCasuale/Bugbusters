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
    user_id INT NOT NULL PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE character_sheet (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  nome_file VARCHAR(255),
  versione INT DEFAULT 1,
  tipo_mime VARCHAR(50),
  contenuto LONGBLOB,
  data_caricamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES utenti(id)
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
    bonus_info varchar(100) DEFAULT NULL,
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
    user_id INT NOT NULL PRIMARY KEY,
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
    FOREIGN KEY (master_id) REFERENCES masters (user_id),
    -- number_of_sessions INT DEFAULT 0
    invite_players_code VARCHAR(10) UNIQUE, -- link visibile al master per invitare nuovi giocatori nella campagna
    invite_masters_code VARCHAR(10) UNIQUE -- questo link è visibile al master se presente per trasferire il controllo da un master all'altro o è visibile ai giocatori se il master non è più presente nella campagna
);

CREATE TABLE campaign_players (
    campaign_id INT NOT NULL,
    character_id INT NOT NULL,
    PRIMARY KEY (campaign_id, character_id),
    FOREIGN KEY (campaign_id) REFERENCES campaigns (id),
    FOREIGN KEY (character_id) REFERENCES character_sheet (id)
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
    WHERE campaign_id IN (SELECT id FROM campaigns WHERE master_id = OLD.user_id)
    LIMIT 1;

    -- Proceed if a valid player_id is found
    IF new_player_id IS NOT NULL THEN
        -- Insert a new row into the masters table using the player_id
        INSERT INTO masters (user_id)
        VALUES (new_player_id);

        -- Update the campaigns table, setting the new master_id to the newly inserted master
        UPDATE campaigns 
        SET master_id = (SELECT MAX(id) FROM masters WHERE user_id = new_player_id)
        WHERE master_id = OLD.user_id;
    END IF;
END$$

DELIMITER ;