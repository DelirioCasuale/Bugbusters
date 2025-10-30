-- Utenti Master e loro Ruolo/Profilo
INSERT INTO users (username, email, password_hash) VALUES ('master1', 'master1@example.com', '$2b$12$ARTE3h9UHw.489IKP/992.yvkPj4pJSkVj3JaK.ZoQsE8cb6fzynW');
INSERT INTO users (username, email, password_hash) VALUES ('master2', 'master2@example.com', '$2b$12$u2TTG868DpcDF2nSDEhKJeu6AODFWgQ4Uulzgc/gff60rDC9w33gy');
INSERT INTO users (username, email, password_hash) VALUES ('master3', 'master3@example.com', '$2b$12$HrL9dv78lxqL2qfJC8UDpuLa71iMfeme8/a/O1rEwjmSa2AyO8m4.');
INSERT INTO users (username, email, password_hash) VALUES ('master4', 'master4@example.com', '$2b$12$K2NPXq97DJdNvNsSoxkyVeH.IDcHpiaklUXeEwULgUxOSn9GwKh1i');
INSERT INTO users (username, email, password_hash) VALUES ('master5', 'master5@example.com', '$2b$12$QLN45mRwOgE.AGq4hexVtuTmOptEv0LTEIWJUa4d3th3fAV4B4t46');
INSERT INTO users (username, email, password_hash) VALUES ('master6', 'master6@example.com', '$2b$12$JYSp9hl0A3pD.fQHNvX3Le7gvLBqur3ABQZ00K3PLHGInMsRTabW6');
INSERT INTO users (username, email, password_hash) VALUES ('master7', 'master7@example.com', '$2b$12$h4XDHUErXHpoDl58gc4X.e3Xu617s8f0IulMcp9XNS.viDd.hwnD.');
INSERT INTO users (username, email, password_hash) VALUES ('master8', 'master8@example.com', '$2b$12$HdYGFUSnbb6l8Hr72ACW.OJnUgRXno.A/fAIW6MwaDyIIXswp4F3a');
INSERT INTO users (username, email, password_hash) VALUES ('master9', 'master9@example.com', '$2b$12$lYiKkHn3.eM9uuQAelAnlOi89RRn3arjSvAssBg7jl27OF4013Pj.');
INSERT INTO users (username, email, password_hash) VALUES ('master10', 'master10@example.com', '$2b$12$ziReLFe5uIrOAd9/BnR5mOi/63r9soadYS6p5v6vP1SRgs/dYJyWe');
INSERT INTO users_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.username LIKE 'master%' AND r.role_name='ROLE_USER';
INSERT INTO masters (user_id) SELECT id FROM users WHERE username LIKE 'master%';

-- Utenti Player e loro Ruolo/Profilo
INSERT INTO users (username, email, password_hash) VALUES ('player1', 'player1@example.com', '$2b$12$vjI1.FXP98IS/fqxvPNKP.eLzbK1XRzb0pT1csBNmL33pfDAZ588S');
INSERT INTO users (username, email, password_hash) VALUES ('player2', 'player2@example.com', '$2b$12$9hvbcGymoieNmQ44H/ZbyelJAiEHk2xM9pOku0W.PwDXY4tPXz7pu');
INSERT INTO users (username, email, password_hash) VALUES ('player3', 'player3@example.com', '$2b$12$f73yW1N6Alrl2tnBjF30zOzAjqtBR6hsWXv8nmqK5OXX2SwmrWL9u');
INSERT INTO users (username, email, password_hash) VALUES ('player4', 'player4@example.com', '$2b$12$Ig3.IIdVKETn6FD5bB8Wu.iNG5QfKb68S/X02cbwkuy0PItlND8ri');
INSERT INTO users (username, email, password_hash) VALUES ('player5', 'player5@example.com', '$2b$12$ALcK8rYiC1sc7U7krBHLxOIWtpkiipaFG0LE8hJqvne4qKapH0Iy2');
INSERT INTO users (username, email, password_hash) VALUES ('player6', 'player6@example.com', '$2b$12$QAbxhoqMkDuHCluiZUyPNO69Gc1kTWPmsQEHuBSc02Bd9rrCO4scy');
INSERT INTO users (username, email, password_hash) VALUES ('player7', 'player7@example.com', '$2b$12$deqkPNDebw5CZd9bi6q2oe35EzHZidylSZi6b8WAbJlLHzOTigyvW');
INSERT INTO users (username, email, password_hash) VALUES ('player8', 'player8@example.com', '$2b$12$979XuBlJtkUA1a94s7o.w.feDdn6Gi1FM1bd1fseh0yth5yZr3fMy');
INSERT INTO users (username, email, password_hash) VALUES ('player9', 'player9@example.com', '$2b$12$dHCD9gpX3Esa2vXB63c8GeAAmiOMcXnZk6NDnhrT8Ew8PtRxY9Sna');
INSERT INTO users (username, email, password_hash) VALUES ('player10', 'player10@example.com', '$2b$12$7dbdxYAxoDJ7Uveyb7eZIuYXSfqbmij4EQjBQWkGfimfrPTRPSYtK');
INSERT INTO users (username, email, password_hash) VALUES ('player11', 'player11@example.com', '$2b$12$Bvynmrw294faV70nokk5cu/SOYMXIEXOzCz.WZ.Xe/btmHHxs9YBO');
INSERT INTO users (username, email, password_hash) VALUES ('player12', 'player12@example.com', '$2b$12$.m3wYL6pBrylOTPMX99tEu83i8bDWEAASJE6TTz7rC0f1FZ3pYwMu');
INSERT INTO users (username, email, password_hash) VALUES ('player13', 'player13@example.com', '$2b$12$Mf.nn.dRz0KyuL5GSAbBKuMXmcggYbV6sgOZNNmlakEfTL7NIhS/K');
INSERT INTO users (username, email, password_hash) VALUES ('player14', 'player14@example.com', '$2b$12$RqGx7pFaj0pjXRJExtCI0OGvjz7iH4r8lVo8WDe4DTJGhild.nIPG');
INSERT INTO users (username, email, password_hash) VALUES ('player15', 'player15@example.com', '$2b$12$pSYDAK3cF2qeqvhSZkN0Mei8tJhnnU8kXeN8GykjNAlyHKf4ietRy');
INSERT INTO users (username, email, password_hash) VALUES ('player16', 'player16@example.com', '$2b$12$OGSTo3gBzambZMkkzwx1CeIYVVQ6k0WYdXUzZocmNS5IBgBSBWZvS');
INSERT INTO users (username, email, password_hash) VALUES ('player17', 'player17@example.com', '$2b$12$Z4m3eqgtVP3ek2l/byEMCOWao2WxariUJcW.2WhPLCZneQCbVlK46');
INSERT INTO users (username, email, password_hash) VALUES ('player18', 'player18@example.com', '$2b$12$TtAjNMZzrwzZlyQzPaXUBejLC9CUMCQy6J0evFQEmqAd3jcLbxRmy');
INSERT INTO users (username, email, password_hash) VALUES ('player19', 'player19@example.com', '$2b$12$fID3MnFO/kTRuRsYbGjBief6AXgKXM8Iu8xAoacnza.rhJp02yeCG');
INSERT INTO users (username, email, password_hash) VALUES ('player20', 'player20@example.com', '$2b$12$1ZXty3YkrzxDSS2gLusI0uijYph4lePAjL63L.CtMFkTFf8J9QKGu');
INSERT INTO users (username, email, password_hash) VALUES ('player21', 'player21@example.com', '$2b$12$Fbss.ue.iyApI/2UJqiY4eKW/JJwew5qRUgNdDuXyAN5YilmJQCcy');
INSERT INTO users (username, email, password_hash) VALUES ('player22', 'player22@example.com', '$2b$12$G2e/vgKtIZob1ZSwXqZiAen/U4qSlDnjgZ6yhWCWv26GzqLZp1.z2');
INSERT INTO users (username, email, password_hash) VALUES ('player23', 'player23@example.com', '$2b$12$OTK7oKBV0cEEwiz9KNA.quaGIMi.QacAO5NVSwpQWTUyH5wXpMGdO');
INSERT INTO users (username, email, password_hash) VALUES ('player24', 'player24@example.com', '$2b$12$wSnW3i3O.7P.1preSk6PmuP/oKFtt.7i9P0DOgSJndXRZXMRYf.Bi');
INSERT INTO users (username, email, password_hash) VALUES ('player25', 'player25@example.com', '$2b$12$IGezAfLWLcyoO6RUTU7V9.L3kcpMdO/II4JDAczT3W5AYk.A45rHW');
INSERT INTO users (username, email, password_hash) VALUES ('player26', 'player26@example.com', '$2b$12$Jzn1jq2aioCdgYbpYfKB/.59ewy0lV35cTfgwPlS7L37iZVz.4m2i');
INSERT INTO users (username, email, password_hash) VALUES ('player27', 'player27@example.com', '$2b$12$.BnUFNawbb1DcTdsJPPgU.nJbgeaAHBZsN.u3xzZfLNQG54c9YKAO');
INSERT INTO users (username, email, password_hash) VALUES ('player28', 'player28@example.com', '$2b$12$pp/9IQxE3R5gLzhgzYyA.ekbwzbIw8eTrHavoD/4PmXtD4maJnnuG');
INSERT INTO users (username, email, password_hash) VALUES ('player29', 'player29@example.com', '$2b$12$bBmAeASM0TsXt7/gI8N7Ge6GOnrO3ercFCAGxUYtWf09q9MIA2TbC');
INSERT INTO users (username, email, password_hash) VALUES ('player30', 'player30@example.com', '$2b$12$W4aJoHiR6yamqhLfIeHPU.QxBd821.zhqXSP1g8BZgAipKqXLJF0G');
INSERT INTO users (username, email, password_hash) VALUES ('player31', 'player31@example.com', '$2b$12$FxD2ALH1iEBM0J1OrKmEBOJZiVnwTl3sehYqt6jw9NLuKxkAmUiGK');
INSERT INTO users (username, email, password_hash) VALUES ('player32', 'player32@example.com', '$2b$12$YD0h79YF5XGgNQ5s2Xg15O.iOpg2U3Ueh3qbK51XZ8T2ZBjjCnC.e');
INSERT INTO users (username, email, password_hash) VALUES ('player33', 'player33@example.com', '$2b$12$ldrNUFQoS.SBKr3QsyDcRe4IHeEQaFXtlXz796ME1OEIBIvGM/tVq');
INSERT INTO users (username, email, password_hash) VALUES ('player34', 'player34@example.com', '$2b$12$wpesBors/k/7czBPb3F9eeHET5eOcup9K2Vh8/7CqLLR1TV0JJ8O2');
INSERT INTO users (username, email, password_hash) VALUES ('player35', 'player35@example.com', '$2b$12$r/QOmtJkq6U8ozy/43yA3.7PNCqiaEtCj0XuuCZwAb9moYMkXFwY6');
INSERT INTO users (username, email, password_hash) VALUES ('player36', 'player36@example.com', '$2b$12$nf7amyUc.G95ix.oCRrXUeuZW7cxyBB/WpPB56Gzyg6ouGyUbFK7i');
INSERT INTO users (username, email, password_hash) VALUES ('player37', 'player37@example.com', '$2b$12$Vplnw.9.3BCdxz4WbTukfe/zmCphI8ruh3/QRTTT4kI4vq8tUZrVG');
INSERT INTO users (username, email, password_hash) VALUES ('player38', 'player38@example.com', '$2b$12$MUyrxI0jgI.oQpTPameZHeByfwbb9pzvt7XJ1DrM8H0MSSWURNZrW');
INSERT INTO users (username, email, password_hash) VALUES ('player39', 'player39@example.com', '$2b$12$xohnWL5L7wpXwicrc3MMoex5mO7Wrj4tMK8lk7MsX8dvV/mjFG4pO');
INSERT INTO users (username, email, password_hash) VALUES ('player40', 'player40@example.com', '$2b$12$LS2zdMO8lTDE9s3fT5LxQuomE9gtCVBFPqA7key9Ei5SiGpGve9o2');
INSERT INTO users_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.username LIKE 'player%' AND r.role_name='ROLE_USER';
INSERT INTO players (user_id) SELECT id FROM users WHERE username LIKE 'player%';

-- Campagne
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master4')), 'Campaign_1', 'Adventure 1', '2025-11-14', '2025-11-21', 'PL001', 'MS001');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master1')), 'Campaign_2', 'Adventure 2', '2025-11-10', '2025-11-17', 'PL002', 'MS002');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master2')), 'Campaign_3', 'Adventure 3', '2025-11-14', '2025-11-21', 'PL003', 'MS003');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master10')), 'Campaign_4', 'Adventure 4', '2025-11-01', '2025-11-08', 'PL004', 'MS004');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master7')), 'Campaign_5', 'Adventure 5', '2025-11-04', '2025-11-11', 'PL005', 'MS005');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master9')), 'Campaign_6', 'Adventure 6', '2025-11-05', '2025-11-12', 'PL006', 'MS006');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master8')), 'Campaign_7', 'Adventure 7', '2025-11-06', '2025-11-13', 'PL007', 'MS007');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master8')), 'Campaign_8', 'Adventure 8', '2025-11-05', '2025-11-12', 'PL008', 'MS008');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master2')), 'Campaign_9', 'Adventure 9', '2025-11-15', '2025-11-22', 'PL009', 'MS009');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master8')), 'Campaign_10', 'Adventure 10', '2025-11-15', '2025-11-22', 'PL010', 'MS010');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master8')), 'Campaign_11', 'Adventure 11', '2025-11-08', '2025-11-15', 'PL011', 'MS011');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master6')), 'Campaign_12', 'Adventure 12', '2025-11-03', '2025-11-10', 'PL012', 'MS012');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master1')), 'Campaign_13', 'Adventure 13', '2025-11-03', '2025-11-10', 'PL013', 'MS013');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master8')), 'Campaign_14', 'Adventure 14', '2025-11-13', '2025-11-20', 'PL014', 'MS014');
INSERT INTO campaigns (master_id, title, description, start_date, scheduled_next_session, invite_players_code, invite_masters_code) VALUES ((SELECT user_id FROM masters WHERE user_id=(SELECT id FROM users WHERE username='master10')), 'Campaign_15', 'Adventure 15', '2025-11-11', '2025-11-18', 'PL015', 'MS015');

-- Schede Personaggio con ALLINEAMENTI CORRETTI
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player1')),
        'Hero_1', 'Wizard', 3, 'NEUTRAL_EVIL', 'DWARF', 'Criminal', -- CORRETTO
        460, 13, 12, 16, 15, 10, 10,
        2, 33, 55, 14, 3, 27);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player2')),
        'Hero_2', 'Paladin', 4, 'NEUTRAL_GOOD', 'ELF', 'Sage', -- CORRETTO
        1352, 8, 8, 16, 11, 8, 15,
        2, 13, 21, 16, 1, 35);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player3')),
        'Hero_3', 'Monk', 4, 'NEUTRAL_GOOD', 'HALF_ORC', 'Criminal', -- CORRETTO
        1109, 12, 11, 14, 18, 12, 17,
        2, 16, 13, 10, 1, 30);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player4')),
        'Hero_4', 'Bard', 4, 'LAWFUL_EVIL', 'ORC', 'Criminal', -- CORRETTO
        2902, 14, 8, 17, 15, 12, 15,
        2, 59, 5, 14, 4, 28);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player5')),
        'Hero_5', 'Cleric', 5, 'LAWFUL_GOOD', 'TIEFLING', 'Acolyte', -- CORRETTO
        533, 16, 10, 13, 14, 8, 17,
        2, 41, 32, 14, 2, 26);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player6')),
        'Hero_6', 'Barbarian', 1, 'NEUTRAL_EVIL', 'TIEFLING', 'Soldier', -- CORRETTO
        2232, 10, 15, 12, 17, 8, 11,
        2, 20, 35, 18, 0, 35);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player7')),
        'Hero_7', 'Wizard', 1, 'CHAOTIC_NEUTRAL', 'HALFLING', 'Soldier', -- CORRETTO
        3686, 8, 11, 11, 17, 18, 13,
        2, 45, 13, 16, 5, 33);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player8')),
        'Hero_8', 'Barbarian', 5, 'NEUTRAL_EVIL', 'ELF', 'Soldier', -- CORRETTO
        1082, 17, 17, 13, 9, 18, 15,
        2, 27, 57, 17, 1, 26);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player9')),
        'Hero_9', 'Cleric', 1, 'CHAOTIC_GOOD', 'TIEFLING', 'Guild Artisan', -- CORRETTO
        352, 11, 16, 17, 13, 12, 10,
        2, 24, 53, 16, 2, 31);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player10')),
        'Hero_10', 'Rogue', 4, 'TRUE_NEUTRAL', 'DWARF', 'Soldier', -- CORRETTO
        3941, 8, 11, 13, 18, 15, 8,
        2, 11, 17, 16, 3, 34);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player11')),
        'Hero_11', 'Bard', 1, 'CHAOTIC_EVIL', 'HALFLING', 'Guild Artisan', -- CORRETTO
        4597, 8, 14, 16, 12, 8, 10,
        2, 55, 41, 18, 1, 25);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player12')),
        'Hero_12', 'Bard', 5, 'LAWFUL_GOOD', 'ORC', 'Entertainer', -- CORRETTO
        3582, 14, 17, 10, 17, 15, 18,
        2, 60, 48, 13, 1, 26);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player13')),
        'Hero_13', 'Barbarian', 1, 'NEUTRAL_EVIL', 'TIEFLING', 'Criminal', -- CORRETTO
        4923, 10, 17, 18, 18, 17, 13,
        2, 38, 60, 13, 5, 33);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player14')),
        'Hero_14', 'Ranger', 5, 'LAWFUL_NEUTRAL', 'TIEFLING', 'Noble', -- CORRETTO
        2866, 17, 13, 13, 9, 18, 15,
        2, 46, 38, 13, 4, 27);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player15')),
        'Hero_15', 'Warlock', 5, 'NEUTRAL_EVIL', 'DRAGONBORN', 'Acolyte', -- CORRETTO
        136, 18, 12, 18, 10, 8, 11,
        2, 57, 7, 14, 3, 28);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player16')),
        'Hero_16', 'Ranger', 5, 'NEUTRAL_GOOD', 'DWARF', 'Sage', -- CORRETTO
        2562, 14, 12, 17, 14, 9, 13,
        2, 59, 12, 13, 5, 31);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player17')),
        'Hero_17', 'Paladin', 4, 'TRUE_NEUTRAL', 'GNOME', 'Guild Artisan', -- CORRETTO
        1472, 16, 11, 16, 8, 12, 8,
        2, 26, 49, 14, 0, 28);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player18')),
        'Hero_18', 'Cleric', 3, 'CHAOTIC_EVIL', 'HALFLING', 'Soldier', -- CORRETTO
        673, 12, 18, 11, 16, 11, 14,
        2, 58, 28, 12, 1, 32);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player19')),
        'Hero_19', 'Bard', 2, 'TRUE_NEUTRAL', 'HUMAN', 'Guild Artisan', -- CORRETTO
        4278, 9, 9, 12, 12, 14, 17,
        2, 25, 28, 17, 5, 28);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player20')),
        'Hero_20', 'Cleric', 5, 'NEUTRAL_EVIL', 'HALF_ELF', 'Entertainer', -- CORRETTO
        3988, 18, 16, 9, 16, 14, 14,
        2, 27, 52, 12, 0, 31);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player21')),
        'Hero_21', 'Wizard', 2, 'CHAOTIC_GOOD', 'DWARF', 'Hermit', -- CORRETTO
        3204, 18, 17, 16, 12, 18, 9,
        2, 47, 54, 11, 4, 26);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player22')),
        'Hero_22', 'Paladin', 4, 'TRUE_NEUTRAL', 'DWARF', 'Urchin', -- CORRETTO
        301, 9, 16, 17, 11, 13, 16,
        2, 60, 51, 18, 5, 35);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player23')),
        'Hero_23', 'Wizard', 5, 'LAWFUL_NEUTRAL', 'GNOME', 'Noble', -- CORRETTO
        1661, 10, 12, 15, 15, 8, 11,
        2, 33, 8, 13, 1, 25);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player24')),
        'Hero_24', 'Wizard', 2, 'CHAOTIC_NEUTRAL', 'DWARF', 'Acolyte', -- CORRETTO
        2073, 9, 15, 9, 16, 17, 17,
        2, 60, 5, 13, 5, 35);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player25')),
        'Hero_25', 'Rogue', 3, 'NEUTRAL_EVIL', 'ORC', 'Hermit', -- CORRETTO
        3559, 14, 10, 9, 13, 12, 12,
        2, 54, 45, 13, 0, 30);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player26')),
        'Hero_26', 'Warlock', 5, 'CHAOTIC_NEUTRAL', 'DRAGONBORN', 'Urchin', -- CORRETTO
        2542, 16, 8, 14, 18, 13, 9,
        2, 34, 50, 14, 1, 32);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player27')),
        'Hero_27', 'Warlock', 4, 'LAWFUL_NEUTRAL', 'DRAGONBORN', 'Soldier', -- CORRETTO
        4531, 10, 8, 9, 12, 11, 18,
        2, 60, 59, 13, 4, 30);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player28')),
        'Hero_28', 'Paladin', 5, 'LAWFUL_NEUTRAL', 'HUMAN', 'Entertainer', -- CORRETTO
        122, 12, 14, 15, 16, 15, 15,
        2, 58, 34, 10, 3, 27);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player29')),
        'Hero_29', 'Bard', 1, 'LAWFUL_EVIL', 'ELF', 'Hermit', -- CORRETTO
        625, 12, 18, 11, 18, 14, 13,
        2, 55, 20, 11, 1, 32);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player30')),
        'Hero_30', 'Wizard', 1, 'CHAOTIC_NEUTRAL', 'TIEFLING', 'Soldier', -- CORRETTO
        1613, 11, 11, 13, 9, 16, 14,
        2, 50, 6, 11, 0, 30);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player31')),
        'Hero_31', 'Ranger', 4, 'LAWFUL_EVIL', 'ELF', 'Hermit', -- CORRETTO
        704, 17, 17, 11, 17, 14, 16,
        2, 47, 36, 13, 0, 30);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player32')),
        'Hero_32', 'Monk', 2, 'NEUTRAL_EVIL', 'HALF_ELF', 'Criminal', -- CORRETTO
        1859, 10, 10, 9, 8, 17, 12,
        2, 28, 27, 14, 3, 32);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player33')),
        'Hero_33', 'Paladin', 2, 'LAWFUL_NEUTRAL', 'DRAGONBORN', 'Guild Artisan', -- CORRETTO
        2751, 8, 16, 14, 13, 12, 10,
        2, 22, 45, 13, 4, 26);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player34')),
        'Hero_34', 'Wizard', 4, 'CHAOTIC_GOOD', 'ORC', 'Hermit', -- CORRETTO
        210, 10, 13, 10, 10, 15, 11,
        2, 22, 36, 11, 2, 28);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player35')),
        'Hero_35', 'Barbarian', 4, 'NEUTRAL_GOOD', 'DWARF', 'Criminal', -- CORRETTO
        596, 11, 13, 17, 15, 15, 17,
        2, 60, 15, 14, 2, 27);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player36')),
        'Hero_36', 'Fighter', 2, 'CHAOTIC_GOOD', 'HALF_ORC', 'Sage', -- CORRETTO
        3253, 8, 17, 16, 8, 18, 16,
        2, 60, 29, 11, 2, 30);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player37')),
        'Hero_37', 'Bard', 2, 'LAWFUL_NEUTRAL', 'TIEFLING', 'Soldier', -- CORRETTO
        169, 11, 8, 10, 11, 12, 14,
        2, 19, 57, 10, 2, 29);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player38')),
        'Hero_38', 'Rogue', 4, 'CHAOTIC_GOOD', 'ELF', 'Guild Artisan', -- CORRETTO
        3209, 17, 8, 14, 14, 17, 10,
        2, 32, 51, 18, 0, 34);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player39')),
        'Hero_39', 'Bard', 3, 'CHAOTIC_GOOD', 'HALFLING', 'Acolyte', -- CORRETTO
        948, 15, 12, 17, 9, 9, 18,
        2, 50, 20, 10, 0, 35);
INSERT INTO character_sheet (player_id, name, primary_class, primary_level, alignment, race, background,
        experience_points, strength, dexterity, constitution, intelligence, wisdom, charisma,
        proficiency_bonus, max_hit_points, current_hit_points, armor_class, initiative, speed)
        VALUES ((SELECT user_id FROM players WHERE user_id=(SELECT id FROM users WHERE username='player40')),
        'Hero_40', 'Ranger', 1, 'NEUTRAL_EVIL', 'HALFLING', 'Criminal', -- CORRETTO
        4055, 13, 16, 12, 15, 15, 12,
        2, 48, 33, 18, 2, 34);

-- Collegamenti Campagna-Giocatore
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_1'), (SELECT id FROM character_sheet WHERE name='Hero_23'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_5'), (SELECT id FROM character_sheet WHERE name='Hero_7'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_6'), (SELECT id FROM character_sheet WHERE name='Hero_28'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_3'), (SELECT id FROM character_sheet WHERE name='Hero_4'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_10'), (SELECT id FROM character_sheet WHERE name='Hero_10'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_7'), (SELECT id FROM character_sheet WHERE name='Hero_30'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_5'), (SELECT id FROM character_sheet WHERE name='Hero_2'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_3'), (SELECT id FROM character_sheet WHERE name='Hero_39'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_13'), (SELECT id FROM character_sheet WHERE name='Hero_32'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_3'), (SELECT id FROM character_sheet WHERE name='Hero_15'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_9'), (SELECT id FROM character_sheet WHERE name='Hero_34'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_7'), (SELECT id FROM character_sheet WHERE name='Hero_18'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_11'), (SELECT id FROM character_sheet WHERE name='Hero_1'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_1'), (SELECT id FROM character_sheet WHERE name='Hero_26'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_6'), (SELECT id FROM character_sheet WHERE name='Hero_9'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_13'), (SELECT id FROM character_sheet WHERE name='Hero_3'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_5'), (SELECT id FROM character_sheet WHERE name='Hero_27'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_11'), (SELECT id FROM character_sheet WHERE name='Hero_37'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_5'), (SELECT id FROM character_sheet WHERE name='Hero_31'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_2'), (SELECT id FROM character_sheet WHERE name='Hero_24'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_3'), (SELECT id FROM character_sheet WHERE name='Hero_38'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_15'), (SELECT id FROM character_sheet WHERE name='Hero_16'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_9'), (SELECT id FROM character_sheet WHERE name='Hero_6'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_15'), (SELECT id FROM character_sheet WHERE name='Hero_21'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_13'), (SELECT id FROM character_sheet WHERE name='Hero_11'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_6'), (SELECT id FROM character_sheet WHERE name='Hero_19'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_1'), (SELECT id FROM character_sheet WHERE name='Hero_22'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_12'), (SELECT id FROM character_sheet WHERE name='Hero_5'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_12'), (SELECT id FROM character_sheet WHERE name='Hero_25'));
INSERT INTO campaign_players (campaign_id, character_id) VALUES ((SELECT id FROM campaigns WHERE title='Campaign_14'), (SELECT id FROM character_sheet WHERE name='Hero_35'));