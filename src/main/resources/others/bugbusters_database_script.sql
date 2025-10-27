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