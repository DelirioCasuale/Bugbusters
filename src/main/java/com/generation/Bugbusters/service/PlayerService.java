package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.CampaignDetailViewDTO;
import com.generation.Bugbusters.dto.CampaignPlayerDTO;
import com.generation.Bugbusters.dto.CharacterSheetCreateRequest;
import com.generation.Bugbusters.dto.CharacterSheetDTO;
import com.generation.Bugbusters.dto.CharacterSheetSimpleDTO;
import com.generation.Bugbusters.dto.JoinCampaignRequest;
import com.generation.Bugbusters.dto.JoinedCampaignDTO;
import com.generation.Bugbusters.dto.MessageResponse;
import com.generation.Bugbusters.dto.PlayerSessionProposalDTO;
import com.generation.Bugbusters.entity.Campaign;
import com.generation.Bugbusters.entity.CharacterSheet;
import com.generation.Bugbusters.entity.Player;
import com.generation.Bugbusters.entity.ProposalVote;
import com.generation.Bugbusters.entity.ProposalVoteId;
import com.generation.Bugbusters.entity.SessionProposal;
import com.generation.Bugbusters.exception.ResourceNotFoundException;
import com.generation.Bugbusters.mapper.CampaignMapper;
// IMPORT MODIFICATO
import com.generation.Bugbusters.mapper.CharacterSheetMapper;
import com.generation.Bugbusters.repository.CampaignRepository;
import com.generation.Bugbusters.repository.CharacterSheetRepository;
import com.generation.Bugbusters.repository.PlayerRepository;
import com.generation.Bugbusters.repository.ProposalVoteRepository;
import com.generation.Bugbusters.repository.SessionProposalRepository;
import com.generation.Bugbusters.security.UserDetailsImpl;
import com.generation.Bugbusters.dto.OrphanedCampaignDTO;
import com.generation.Bugbusters.exception.UnauthorizedException;
// NUOVO IMPORT
import com.generation.Bugbusters.exception.BadRequestException; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    private CharacterSheetRepository characterSheetRepository;
    @Autowired
    private PlayerRepository playerRepository;
    
    // MODIFICA: Ri-aggiunto il CharacterSheetMapper
    // Ci serve per convertire l'entità finale in DTO
    @Autowired
    private CharacterSheetMapper characterSheetMapper; 

    @Autowired
    private CampaignRepository campaignRepository; 
    @Autowired
    private SessionProposalRepository sessionProposalRepository; 
    @Autowired
    private ProposalVoteRepository proposalVoteRepository; 
    @Autowired
    private CampaignMapper campaignMapper; 

    /**
     * MODIFICA COMPLETA:
     * Crea una nuova scheda personaggio per l'utente loggato,
     * applicando le regole di base della classe (Livello 1).
     */
    @Transactional
    public CharacterSheetDTO createCharacterSheet(CharacterSheetCreateRequest dto) {
        // 1. Ottiene il profilo player dell'utente loggato
        Player currentPlayer = getCurrentPlayer();

        // 2. Crea l'entità base
        CharacterSheet newSheet = new CharacterSheet();
        newSheet.setPlayer(currentPlayer);
        newSheet.setName(dto.getName());
        newSheet.setRace(dto.getRace());
        
        // 3. Applica i valori di default (stats 10, lvl 1, ecc.)
        setSheetDefaults(newSheet);

        // 4. Applica le regole della classe (PF, competenze, equip)
        // N.B.: Il DTO passa la classe come Stringa (es. "Barbaro")
        switch (dto.getPrimaryClass().toLowerCase()) {
            case "artefice": applyArtificerRules(newSheet); break;
            case "barbaro": applyBarbarianRules(newSheet); break;
            case "guerriero": applyFighterRules(newSheet); break;
            case "ladro": applyRogueRules(newSheet); break;
            case "monaco": applyMonkRules(newSheet); break;
            case "paladino": applyPaladinRules(newSheet); break;
            case "ranger": applyRangerRules(newSheet); break;
            case "bardo": applyBardRules(newSheet); break;
            case "chierico": applyClericRules(newSheet); break;
            case "druido": applyDruidRules(newSheet); break;
            case "mago": applyWizardRules(newSheet); break;
            case "stregone": applySorcererRules(newSheet); break;
            case "warlock": applyWarlockRules(newSheet); break;
            
            default:
                throw new BadRequestException("Classe '" + dto.getPrimaryClass() + "' non supportata o non valida.");
        }
        
        // 5. Calcola i modificatori (basati sulle stats a 10, quindi +0)
        // (In futuro, qui si ricalcolano HP e AC se le stats cambiano)
        
        // 6. Salva l'entità nel database
        CharacterSheet savedSheet = characterSheetRepository.save(newSheet);

        // 7. Riconverte l'entità salvata in un DTO e la restituisce
        return characterSheetMapper.toDTO(savedSheet);
    }

    // recupera TUTTE le schede personaggio dell'utente loggato
    @Transactional(readOnly = true) // readOnly = true ottimizza le query in sola lettura
    public List<CharacterSheetDTO> getAllMyCharacterSheets() {
        // ottiene il profilo Player dell'utente loggato
        Player currentPlayer = getCurrentPlayer();

        // cerca nel repository tutte le schede di quel player
        List<CharacterSheet> sheets = characterSheetRepository.findByPlayerId(currentPlayer.getId());

        // converte la lista di Entità in una lista di DTO
        return sheets.stream()
                .map(characterSheetMapper::toDTO)
                .collect(Collectors.toList());
    }

    // metodo helper per ottenere il PROFILO PLAYER dell'utente attualmente loggato
    private Player getCurrentPlayer() {
        // ottiene l'id utente dal contesto di sicurezza
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();

        // cerca il profilo player corrispondente
        // l'id del player è lo stesso dell'user
        return playerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Profilo Player non trovato per l'utente loggato."));
    }

    // permette al Player loggato di unirsi a una campagna tramite codice invito
    @Transactional
    public ResponseEntity<?> joinCampaign(JoinCampaignRequest dto) {
        
        // ottiene il Player loggato
        Player currentPlayer = getCurrentPlayer();

        // trova la Campagna tramite il codice
        Optional<Campaign> campaignOpt = 
                campaignRepository.findByInvitePlayersCode(dto.getInviteCode());

        if (!campaignOpt.isPresent()) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: Codice invito non valido."), 
                    HttpStatus.NOT_FOUND);
        }
        Campaign campaign = campaignOpt.get();

        // trova la Scheda Personaggio
        Optional<CharacterSheet> sheetOpt = 
                characterSheetRepository.findById(dto.getCharacterSheetId());

        if (!sheetOpt.isPresent()) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: Scheda personaggio non trovata."), 
                    HttpStatus.NOT_FOUND);
        }
        CharacterSheet sheet = sheetOpt.get();

        // controlla che la scheda appartenga al giocatore loggato
        if (!sheet.getPlayer().getId().equals(currentPlayer.getId())) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: Non puoi unirti con una scheda che non è tua."), 
                    HttpStatus.FORBIDDEN);
        }

        // controlla che il giocatore non sia già in questa campagna
        // (Un giocatore non può essere in una campagna con due personaggi diversi)
        boolean alreadyJoined = campaign.getPlayers().stream()
                .anyMatch(s -> s.getPlayer().getId().equals(currentPlayer.getId()));
        
        if (alreadyJoined) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: Fai già parte di questa campagna."), 
                    HttpStatus.BAD_REQUEST);
        }

        // aggiunge la scheda alla campagna
        campaign.getPlayers().add(sheet);
        campaignRepository.save(campaign); // JPA aggiornerà la tabella campaign_players

        return ResponseEntity.ok(
                new MessageResponse("Ti sei unito alla campagna '" + campaign.getTitle() + "'!"));
    }

    // recupera le campagne a cui il giocatore loggato si è unito
    @Transactional(readOnly = true)
    public List<JoinedCampaignDTO> getMyJoinedCampaigns() {
        
        // ottiene il Player loggato
        Player currentPlayer = getCurrentPlayer();
        Long playerId = currentPlayer.getId();

        // trova le campagne usando la nostra query custom
        List<Campaign> campaigns = campaignRepository.findCampaignsByPlayerId(playerId);

        // mappa i risultati (lista di Entità) in DTO
        return campaigns.stream().map(campaign -> {
            
            // trova la scheda specifica che questo giocatore sta usando in QUESTA campagna
            CharacterSheet sheetInUse = campaign.getPlayers().stream()
                    .filter(sheet -> sheet.getPlayer().getId().equals(playerId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Logica corrotta: campagna trovata senza scheda del giocatore"));

            // crea il DTO della scheda
            CharacterSheetSimpleDTO sheetDTO = new CharacterSheetSimpleDTO();
            sheetDTO.setId(sheetInUse.getId());
            sheetDTO.setName(sheetInUse.getName());

            // crea il DTO della campagna
            JoinedCampaignDTO dto = new JoinedCampaignDTO();
            dto.setCampaignId(campaign.getId());
            dto.setCampaignTitle(campaign.getTitle());
            dto.setCharacterUsed(sheetDTO);
            
            return dto;
            
        }).collect(Collectors.toList());
    }

    /**
     * recupera tutte le proposte di sessione attive per tutte le campagne a cui il Player partecipa
     */
    @Transactional(readOnly = true)
    public List<PlayerSessionProposalDTO> getActiveProposals() {
        Player currentPlayer = getCurrentPlayer();
        Long playerId = currentPlayer.getId();

        // trova le campagne del giocatore
        List<Campaign> myCampaigns = campaignRepository.findCampaignsByPlayerId(playerId);

        // colleziona tutte le proposte da tutte le campagne
        List<PlayerSessionProposalDTO> allProposals = new ArrayList<>();

        for (Campaign campaign : myCampaigns) {
            // trova le proposte attive per QUESTA campagna
            List<SessionProposal> activeProposals = sessionProposalRepository
                    .findByCampaignIdAndExpiresOnAfterAndIsConfirmedFalse(
                            campaign.getId(), 
                            LocalDateTime.now());
            
            // mappa le proposte nel DTO
            for (SessionProposal proposal : activeProposals) {
                
                // controlla se il giocatore ha già votato
                // (grazie a @Transactional, proposal.getVotes() viene caricato)
                boolean hasVoted = proposal.getVotes().stream()
                        .anyMatch(vote -> vote.getPlayer().getId().equals(playerId));

                // costruisce il DTO
                allProposals.add(mapProposalToPlayerDTO(proposal, campaign, hasVoted));
            }
        }
        
        return allProposals;
    }

    /**
     * registra il voto del Player loggato per una proposta
     */
    @Transactional
    public ResponseEntity<?> voteForProposal(Long proposalId) {
        Player currentPlayer = getCurrentPlayer();
        Long playerId = currentPlayer.getId();

        // trova la proposta
        SessionProposal proposal = sessionProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proposta non trovata con ID: " + proposalId));

        // verifica se è ancora attiva
        if (proposal.isConfirmed() || proposal.getExpiresOn().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: La votazione per questa proposta è chiusa."), 
                    HttpStatus.BAD_REQUEST);
        }

        // verifica se il player è in questa campagna
        Campaign campaign = proposal.getCampaign();
        boolean isMember = campaign.getPlayers().stream()
                .anyMatch(sheet -> sheet.getPlayer().getId().equals(playerId));
        
        if (!isMember) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: Non fai parte della campagna per cui stai votando."), 
                    HttpStatus.FORBIDDEN);
        }

        // verifica se il player ha già votato
        ProposalVoteId voteId = new ProposalVoteId(proposalId, playerId);
        if (proposalVoteRepository.existsById(voteId)) {
            return new ResponseEntity<>(
                    new MessageResponse("Errore: Hai già votato per questa proposta."), 
                    HttpStatus.BAD_REQUEST);
        }

        // se tutto ok registra il voto
        ProposalVote newVote = new ProposalVote();
        newVote.setId(voteId);
        newVote.setPlayer(currentPlayer);
        newVote.setProposal(proposal);
        
        proposalVoteRepository.save(newVote);

        return ResponseEntity.ok(new MessageResponse("Voto registrato con successo!"));
    }


    /**
     * metodo helper per mappare un'entità proposal nel dto
     */
    private PlayerSessionProposalDTO mapProposalToPlayerDTO(
            SessionProposal proposal, Campaign campaign, boolean hasVoted) {
        
        PlayerSessionProposalDTO dto = new PlayerSessionProposalDTO();
        dto.setProposalId(proposal.getId());
        dto.setCampaignId(campaign.getId());
        dto.setCampaignTitle(campaign.getTitle());
        dto.setProposedDate(proposal.getProposedDate());
        dto.setExpiresOn(proposal.getExpiresOn());
        dto.setConfirmed(proposal.isConfirmed());
        dto.setHasVoted(hasVoted);
        return dto;
    }

    /**
     * recupera la lista delle campagne del giocatore che sono orfane (il master è stato bannato)
     * e in attesa di un nuovo master
     */
    @Transactional(readOnly = true)
    public List<OrphanedCampaignDTO> getMyOrphanedCampaigns() {
        
        // ottiene il player loggato
        Player currentPlayer = getCurrentPlayer();

        // trova tutte le campagne a cui il giocatore partecipa
        List<Campaign> myCampaigns = 
                campaignRepository.findCampaignsByPlayerId(currentPlayer.getId());

        LocalDateTime now = LocalDateTime.now();

        // filtra la lista per trovare solo quelle orfane e attive
        return myCampaigns.stream()
            .filter(campaign -> 
                    // il timer di ban è impostato
                    campaign.getMasterBanPendingUntil() != null && 
                    // e non è ancora scaduto
                    campaign.getMasterBanPendingUntil().isAfter(now)
            )
            .map(this::mapToOrphanedCampaignDTO) // usa un helper 
            .collect(Collectors.toList());
    }

    /**
     * metodo helper per mappare campaign in OrphanedCampaignDTO
     */
    private OrphanedCampaignDTO mapToOrphanedCampaignDTO(Campaign campaign) {
        OrphanedCampaignDTO dto = new OrphanedCampaignDTO();
        dto.setCampaignId(campaign.getId());
        dto.setCampaignTitle(campaign.getTitle());
        dto.setInviteMastersCode(campaign.getInviteMastersCode());
        dto.setDeletionDeadline(campaign.getMasterBanPendingUntil());
        return dto;
    }

    /**
     * Recupera i dati completi di una singola scheda, 
     * validando che appartenga al giocatore loggato.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<?> getCharacterSheetDetails(Long sheetId) {
        Player currentPlayer = getCurrentPlayer();
        
        // 1. Trova la scheda
        CharacterSheet sheet = characterSheetRepository.findById(sheetId)
                .orElseThrow(() -> new ResourceNotFoundException("Scheda non trovata con ID: " + sheetId));
        
        // 2. VALIDAZIONE DI SICUREZZA
        if (!sheet.getPlayer().getId().equals(currentPlayer.getId())) {
            throw new UnauthorizedException("Non sei autorizzato a visualizzare questa scheda.");
        }
        
        // 3. Mappa e restituisci
        return ResponseEntity.ok(characterSheetMapper.toDTO(sheet));
    }

    /**
     * Aggiorna i dati di una singola scheda, 
     * validando che appartenga al giocatore loggato.
     */
    @Transactional
    public ResponseEntity<?> updateCharacterSheet(Long sheetId, CharacterSheetDTO dto) {
        Player currentPlayer = getCurrentPlayer();
        
        // 1. Trova la scheda
        CharacterSheet sheet = characterSheetRepository.findById(sheetId)
                .orElseThrow(() -> new ResourceNotFoundException("Scheda non trovata con ID: " + sheetId));
        
        // 2. VALIDAZIONE DI SICUREZZA
        if (!sheet.getPlayer().getId().equals(currentPlayer.getId())) {
            throw new UnauthorizedException("Non sei autorizzato a modificare questa scheda.");
        }
        
        // 3. Aggiorna l'entità usando il mapper e salva
        characterSheetMapper.updateEntityFromDTO(sheet, dto); // Dobbiamo creare questo metodo
        CharacterSheet updatedSheet = characterSheetRepository.save(sheet);
        
        // 4. Restituisci la scheda aggiornata
        return ResponseEntity.ok(characterSheetMapper.toDTO(updatedSheet));
    }

    /**
     * Recupera i dati di una singola campagna per la vista del giocatore.
     * Include info, master, altri giocatori e proposte di voto.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<?> getCampaignDetailsForPlayer(Long campaignId) {
        Player currentPlayer = getCurrentPlayer();
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campagna non trovata"));

        // 1. VALIDAZIONE SICUREZZA
        boolean isMember = campaign.getPlayers().stream()
                .anyMatch(s -> s.getPlayer().getId().equals(currentPlayer.getId()));
        
        if (!isMember) {
            throw new UnauthorizedException("Non sei membro di questa campagna.");
        }
        
        // 2. Costruisci il DTO
        CampaignDetailViewDTO dto = new CampaignDetailViewDTO();
        dto.setId(campaign.getId());
        dto.setTitle(campaign.getTitle());
        dto.setDescription(campaign.getDescription());
        dto.setStartDate(campaign.getStartDate());
        dto.setScheduledNextSession(campaign.getScheduledNextSession());
        
        if (campaign.getMaster() != null) {
            dto.setMasterUsername(campaign.getMaster().getUser().getUsername());
        } else {
            dto.setMasterUsername("Nessun Master Assegnato");
        }

        // 3. Popola i giocatori (Usando il nome DTO corretto 'setPlayers')
        List<CampaignPlayerDTO> playersList = campaign.getPlayers().stream()
                .map(campaignMapper::mapSheetToCampaignPlayerDTO) 
                .collect(Collectors.toList());
        dto.setPlayers(playersList); // <-- Corretto (se avevi 'setFellowPlayers', questo lo corregge)

        // 4. Popola le proposte (LOGICA MODIFICATA)
        LocalDateTime now = LocalDateTime.now();
        
        List<PlayerSessionProposalDTO> allProposals = campaign.getProposals().stream()
                .map(p -> {
                    boolean hasVoted = p.getVotes().stream()
                            .anyMatch(v -> v.getPlayer().getId().equals(currentPlayer.getId()));
                    return mapProposalToPlayerDTO(p, campaign, hasVoted); // Riutilizza l'helper
                })
                .collect(Collectors.toList());

        // Partiziona la lista in "attive" e "passate"
        // Attiva = NON confermata E NON scaduta E NON votata
        Map<Boolean, List<PlayerSessionProposalDTO>> partitionedProposals = allProposals.stream()
            .collect(Collectors.partitioningBy(p -> 
                !p.isConfirmed() &&                 
                p.getExpiresOn().isAfter(now) &&   
                !p.isHasVoted()                    
            ));

        dto.setActiveProposals(partitionedProposals.get(true)); // Chiave 'true' = attive e da votare
        dto.setPastProposals(partitionedProposals.get(false));  // Chiave 'false' = passate (scadute, votate, o confermate)
        
        return ResponseEntity.ok(dto);
    }
    
    // ====================================================================
    // --- NUOVI METODI HELPER PER LA CREAZIONE DELLA CLASSE ---
    // ====================================================================

    /**
     * Imposta i valori di default per ogni nuova scheda (Livello 1)
     */
    private void setSheetDefaults(CharacterSheet entity) {
        entity.setPrimaryLevel(1);
        entity.setExperiencePoints(0);
        
        // Stats (Tutte a 10 = Mod +0)
        entity.setStrength((short) 10);
        entity.setDexterity((short) 10);
        entity.setConstitution((short) 10);
        entity.setIntelligence((short) 10);
        entity.setWisdom((short) 10);
        entity.setCharisma((short) 10);
        
        // Stats derivate (basate su stats +0)
        entity.setProficiencyBonus((short) 2);
        entity.setArmorClass((short) 10); // 10 + Mod Destrezza (0)
        entity.setInitiative((short) 0); // Mod Destrezza (0)
        
        // Abilità (tutte false di default)
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
        
        // (La velocità dipende dalla razza, ma 30 è un buon default)
        entity.setSpeed((short) 30); 
    }

    // --- 13 METODI PER LE REGOLE DELLE CLASSI ---
    
    private void applyArtificerRules(CharacterSheet sheet) {
        sheet.setPrimaryClass("Artefice");
        sheet.setMaxHitPoints(8); // 8 + Mod COS (0)
        sheet.setCurrentHitPoints(8);
        sheet.setFeaturesAndTraits("Dado Vita: 1d8\nPrivilegi: Infusione Arcana, Riparare");
        sheet.setProficienciesAndLanguages("Tiri Salvezza: Costituzione, Intelligenza\nArmature: Armature leggere, armature medie, scudi\nArmi: Armi semplici, Balestre a mano, Balestre pesanti\nStrumenti: Utensili da inventore, Utensili da artigiano (a scelta)");
        sheet.setEquipment("Uno strumento a scelta, un focalizzatore arcano, un'armatura di cuoio.");
        // Abilità (Scegli 2 da Arcana, Storia, Indagare, Medicina, Natura, Percezione)
        sheet.setArcanaSkillProficiency(true);
        sheet.setInvestigationSkillProficiency(true);
    }
    
    private void applyBarbarianRules(CharacterSheet sheet) {
        sheet.setPrimaryClass("Barbaro");
        sheet.setMaxHitPoints(12); // 12 + Mod COS (0)
        sheet.setCurrentHitPoints(12);
        sheet.setFeaturesAndTraits("Dado Vita: 1d12\nPrivilegi: Ira, Difesa Senza Armatura");
        sheet.setProficienciesAndLanguages("Tiri Salvezza: Forza, Costituzione\nArmature: Armature leggere, armature medie, scudi\nArmi: Armi semplici, armi marziali\nStrumenti: Nessuno");
        sheet.setEquipment("Un'ascia bipenne, due asce, quattro giavellotti.");
        // Abilità (Scegli 2 da Addestrare Animali, Atletica, Intimidire, Natura, Percezione, Sopravvivenza)
        sheet.setAthleticsSkillProficiency(true);
        sheet.setIntimidationSkillProficiency(true);
    }

    private void applyFighterRules(CharacterSheet sheet) {
        sheet.setPrimaryClass("Guerriero");
        sheet.setMaxHitPoints(10); // 10 + Mod COS (0)
        sheet.setCurrentHitPoints(10);
        sheet.setFeaturesAndTraits("Dado Vita: 1d10\nPrivilegi: Stile di Combattimento, Secondo Vento");
        sheet.setProficienciesAndLanguages("Tiri Salvezza: Forza, Costituzione\nArmature: Tutte le armature, scudi\nArmi: Armi semplici, armi marziali\nStrumenti: Nessuno");
        sheet.setEquipment("Armatura di maglia, uno spadone, due asce.");
        // Abilità (Scegli 2 da Addestrare Animali, Atletica, Acrobazia, Intimidire, Percezione, Sopravvivenza, Storia)
        sheet.setAthleticsSkillProficiency(true);
        sheet.setPerceptionSkillProficiency(true);
    }

    private void applyRogueRules(CharacterSheet sheet) {
        sheet.setPrimaryClass("Ladro");
        sheet.setMaxHitPoints(8); // 8 + Mod COS (0)
        sheet.setCurrentHitPoints(8);
        sheet.setFeaturesAndTraits("Dado Vita: 1d8\nPrivilegi: Attacco Furtivo (1d6), Gergo Furtivo, Esperienza");
        sheet.setProficienciesAndLanguages("Tiri Salvezza: Destrezza, Intelligenza\nArmature: Armature leggere\nArmi: Armi semplici, balestre a mano, spade lunghe, stocchi, spade corte\nStrumenti: Utensili da scasso");
        sheet.setEquipment("Uno stocco, un arco corto, armatura di cuoio, utensili da scasso.");
        // Abilità (Scegli 4)
        sheet.setStealthSkillProficiency(true);
        sheet.setSleightOfHandSkillProficiency(true);
        sheet.setAcrobaticsSkillProficiency(true);
        sheet.setDeceptionSkillProficiency(true);
    }

    private void applyMonkRules(CharacterSheet sheet) {
        sheet.setPrimaryClass("Monaco");
        sheet.setMaxHitPoints(8); // 8 + Mod COS (0)
        sheet.setCurrentHitPoints(8);
        sheet.setFeaturesAndTraits("Dado Vita: 1d8\nPrivilegi: Difesa Senza Armatura, Arti Marziali");
        sheet.setProficienciesAndLanguages("Tiri Salvezza: Forza, Destrezza\nArmature: Nessuna\nArmi: Armi semplici, spade corte\nStrumenti: Uno strumento da artigiano o uno strumento musicale");
        sheet.setEquipment("Una spada corta, 10 dardi, un'esplorazione.");
        // Abilità (Scegli 2 da Acrobazia, Atletica, Furtività, Intuizione, Religione, Storia)
        sheet.setAcrobaticsSkillProficiency(true);
        sheet.setInsightSkillProficiency(true);
    }
    
    private void applyPaladinRules(CharacterSheet sheet) {
        sheet.setPrimaryClass("Paladino");
        sheet.setMaxHitPoints(10); // 10 + Mod COS (0)
        sheet.setCurrentHitPoints(10);
        sheet.setFeaturesAndTraits("Dado Vita: 1d10\nPrivilegi: Percezione del Divino, Imposizione delle Mani");
        sheet.setProficienciesAndLanguages("Tiri Salvezza: Saggezza, Carisma\nArmature: Tutte le armature, scudi\nArmi: Armi semplici, armi marziali\nStrumenti: Nessuno");
        sheet.setEquipment("Armatura di maglia, uno spadone, un simbolo sacro.");
        // Abilità (Scegli 2 da Atletica, Intuizione, Intimidire, Medicina, Persuasione, Religione)
        sheet.setAthleticsSkillProficiency(true);
        sheet.setPersuasionSkillProficiency(true);
    }

    private void applyRangerRules(CharacterSheet sheet) {
        sheet.setPrimaryClass("Ranger");
        sheet.setMaxHitPoints(10); // 10 + Mod COS (0)
        sheet.setCurrentHitPoints(10);
        sheet.setFeaturesAndTraits("Dado Vita: 1d10\nPrivilegi: Nemico Prescelto, Esploratore Nato");
        sheet.setProficienciesAndLanguages("Tiri Salvezza: Forza, Destrezza\nArmature: Armature leggere, armature medie, scudi\nArmi: Armi semplici, armi marziali\nStrumenti: Nessuno");
        sheet.setEquipment("Armatura di cuoio, due spade corte, un arco lungo.");
        // Abilità (Scegli 3 da Addestrare Animali, Atletica, Furtività, Indagare, Intuizione, Natura, Percezione, Sopravvivenza)
        sheet.setAnimalHandlingSkillProficiency(true);
        sheet.setSurvivalSkillProficiency(true);
        sheet.setStealthSkillProficiency(true);
    }

    private void applyBardRules(CharacterSheet sheet) {
        sheet.setPrimaryClass("Bardo");
        sheet.setMaxHitPoints(8); // 8 + Mod COS (0)
        sheet.setCurrentHitPoints(8);
        sheet.setFeaturesAndTraits("Dado Vita: 1d8\nPrivilegi: Ispirazione Bardica (d6), Incantesimi");
        sheet.setProficienciesAndLanguages("Tiri Salvezza: Destrezza, Carisma\nArmature: Armature leggere\nArmi: Armi semplici, balestre a mano, spade lunghe, stocchi, spade corte\nStrumenti: Tre strumenti musicali a scelta");
        sheet.setEquipment("Uno stocco, un liuto, armatura di cuoio.");
        // Abilità (Qualsiasi tre)
        sheet.setPerformanceSkillProficiency(true);
        sheet.setPersuasionSkillProficiency(true);
        sheet.setDeceptionSkillProficiency(true);
    }

    private void applyClericRules(CharacterSheet sheet) {
        sheet.setPrimaryClass("Chierico");
        sheet.setMaxHitPoints(8); // 8 + Mod COS (0)
        sheet.setCurrentHitPoints(8);
        sheet.setFeaturesAndTraits("Dado Vita: 1d8\nPrivilegi: Dominio Divino, Incantesimi");
        sheet.setProficienciesAndLanguages("Tiri Salvezza: Saggezza, Carisma\nArmature: Armature leggere, armature medie, scudi\nArmi: Armi semplici\nStrumenti: Nessuno");
        sheet.setEquipment("Una mazza, armatura di scaglie, un simbolo sacro.");
        // Abilità (Scegli 2 da Guarire, Intuizione, Persuasione, Religione, Storia)
        sheet.setReligionSkillProficiency(true);
        sheet.setInsightSkillProficiency(true);
    }
    
    private void applyDruidRules(CharacterSheet sheet) {
        sheet.setPrimaryClass("Druido");
        sheet.setMaxHitPoints(8); // 8 + Mod COS (0)
        sheet.setCurrentHitPoints(8);
        sheet.setFeaturesAndTraits("Dado Vita: 1d8\nPrivilegi: Druidico, Incantesimi");
        sheet.setProficienciesAndLanguages("Tiri Salvezza: Intelligenza, Saggezza\nArmature: Armature leggere, armature medie, scudi (non metalliche)\nArmi: Bastoni, dardi, falcetti, fionde, giavellotti, mazze, scimitarre, sicomori\nStrumenti: Kit da erborista");
        sheet.setEquipment("Uno scudo di legno, una scimitarra, un focalizzatore druidico.");
        // Abilità (Scegli 2 da Addestrare Animali, Arcano, Guarire, Intuizione, Natura, Percezione, Religione, Sopravvivenza)
        sheet.setNatureSkillProficiency(true);
        sheet.setAnimalHandlingSkillProficiency(true);
    }

    private void applyWizardRules(CharacterSheet sheet) {
        sheet.setPrimaryClass("Mago");
        sheet.setMaxHitPoints(6); // 6 + Mod COS (0)
        sheet.setCurrentHitPoints(6);
        sheet.setFeaturesAndTraits("Dado Vita: 1d6\nPrivilegi: Recupero Arcano, Incantesimi");
        sheet.setProficienciesAndLanguages("Tiri Salvezza: Intelligenza, Saggezza\nArmature: Nessuna\nArmi: Bastoni, dardi, fionde, pugnali\nStrumenti: Nessuno");
        sheet.setEquipment("Un bastone, un libro degli incantesimi, una sacca per componenti.");
        // Abilità (Scegli 2 da Arcano, Indagare, Intuizione, Guarire, Religione, Storia)
        sheet.setArcanaSkillProficiency(true);
        sheet.setHistorySkillProficiency(true);
    }

    private void applySorcererRules(CharacterSheet sheet) {
        sheet.setPrimaryClass("Stregone");
        sheet.setMaxHitPoints(6); // 6 + Mod COS (0)
        sheet.setCurrentHitPoints(6);
        sheet.setFeaturesAndTraits("Dado Vita: 1d6\nPrivilegi: Origine Stregonesca, Incantesimi, Metamagia");
        sheet.setProficienciesAndLanguages("Tiri Salvezza: Costituzione, Carisma\nArmature: Nessuna\nArmi: Bastoni, dardi, fionde, pugnali\nStrumenti: Nessuno");
        sheet.setEquipment("Una balestra leggera, un focalizzatore arcano, due pugnali.");
        // Abilità (Scegli 2 da Arcano, Decepire, Intimidire, Intuizione, Persuasione, Religione)
        sheet.setArcanaSkillProficiency(true);
        sheet.setDeceptionSkillProficiency(true);
    }

    private void applyWarlockRules(CharacterSheet sheet) {
        sheet.setPrimaryClass("Warlock");
        sheet.setMaxHitPoints(8); // 8 + Mod COS (0)
        sheet.setCurrentHitPoints(8);
        sheet.setFeaturesAndTraits("Dado Vita: 1d8\nPrivilegi: Patto Ultraterreno, Magia del Patto, Suppliche Occulte");
        sheet.setProficienciesAndLanguages("Tiri Salvezza: Saggezza, Carisma\nArmature: Armature leggere\nArmi: Armi semplici\nStrumenti: Nessuno");
        sheet.setEquipment("Una balestra leggera, un focalizzatore arcano, armatura di cuoio.");
        // Abilità (Scegli 2 da Arcano, Decepire, Indagare, Intimidire, Natura, Religione, Storia)
        sheet.setArcanaSkillProficiency(true);
        sheet.setIntimidationSkillProficiency(true);
    }
}