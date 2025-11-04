package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.AdminUserViewDTO;
import com.generation.Bugbusters.dto.MessageResponse;
import com.generation.Bugbusters.entity.Campaign;
import com.generation.Bugbusters.entity.Role;
import com.generation.Bugbusters.entity.User;
import com.generation.Bugbusters.enumeration.RoleName;
import com.generation.Bugbusters.exception.BadRequestException;
import com.generation.Bugbusters.exception.ResourceNotFoundException;
import com.generation.Bugbusters.mapper.UserMapper;
import com.generation.Bugbusters.repository.CampaignRepository;
import com.generation.Bugbusters.repository.RoleRepository;
import com.generation.Bugbusters.repository.UserRepository;
import com.generation.Bugbusters.dto.AdminUserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    // logger per il logging delle operazioni serve per monitorare le azioni
    // amministrative
    // (es. ban utenti, modifiche critiche, ecc)
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper; // iniettiamo il mapper per convertire User in AdminUserViewDTO

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * recupera TUTTI gli utenti nel sistema (PAGINATI)
     */
    @Transactional(readOnly = true)
    public Page<AdminUserViewDTO> getAllUsers(Pageable pageable) {

        Page<User> usersPage = userRepository.findAll(pageable);

        // Usa la funzione .map() di Page per convertire il contenuto
        return usersPage.map(userMapper::toAdminViewDTO);
    }

    /**
     * recupera solo gli utenti che sono player (PAGINATI)
     */
    @Transactional(readOnly = true)
    public Page<AdminUserViewDTO> getPlayersOnly(Pageable pageable) {

        Page<User> usersPage = userRepository.findByPlayerIsNotNull(pageable);

        return usersPage.map(userMapper::toAdminViewDTO);
    }

    /**
     * recupera solo gli utenti che sono master (PAGINATI)
     */
    @Transactional(readOnly = true)
    public Page<AdminUserViewDTO> getMastersOnly(Pageable pageable) {

        Page<User> usersPage = userRepository.findByMasterIsNotNull(pageable);

        return usersPage.map(userMapper::toAdminViewDTO);
    }

    /**
     * banna un utente
     * questa è un'operazione di "sospensione" dell'account per 1 anno
     * se l'utente è un master, avvia il timer di 30 giorni per le sue campagne
     */
    @Transactional // FONDAMENTALE: modifica tabelle users e campaigns
    public ResponseEntity<?> banUser(Long userId) {

        // trova l'utente
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));

        // controllo: non bannare un utente già bannato
        if (user.isBanned()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Errore: Questo utente è già bannato."));
        }

        // applica il ban all'utente
        user.setBanned(true);
        user.setDeletionScheduledOn(LocalDateTime.now().plusYears(1)); // sospeso per 1 anno
        userRepository.save(user);

        logger.info("Utente {} (ID: {}) bannato. Cancellazione programmata per {}.",
                user.getUsername(), user.getId(), user.getDeletionScheduledOn());

        // logica master
        // controlla se l'utente bannato ha un profilo master
        if (user.getMaster() != null) {

            logger.warn("L'utente bannato è un Master. Avvio timer 30gg per le sue campagne.");

            // trova tutte le campagne di questo master
            List<Campaign> campaignsToOrphan = campaignRepository.findByMasterId(user.getMaster().getId());

            LocalDateTime expiryTime = LocalDateTime.now().plusDays(30);

            for (Campaign campaign : campaignsToOrphan) {
                // imposta il timer di 30 giorni per la cancellazione della campagna
                campaign.setMasterBanPendingUntil(expiryTime);
                campaignRepository.save(campaign);

                logger.info("Campagna ID {} impostata in 'pending delete' (30gg).",
                        campaign.getId());
            }
        }

        return ResponseEntity.ok(new MessageResponse("Utente bannato con successo.")); // Semplificato
    }

    /**
     * sblocca (rimuove la sospensione) di un utente.
     * Se l'utente era un master, annulla il timer di cancellazione delle campagne
     * orfane.
     */
    @Transactional // FONDAMENTALE: modifica tabelle users e campaigns
    public ResponseEntity<?> unbanUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));

        // Controllo: non sbloccare un utente attivo
        if (!user.isBanned()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Errore: Questo utente non è bannato."));
        }

        // Annulla il ban
        user.setBanned(false);
        user.setDeletionScheduledOn(null); // Rimuove la data di cancellazione
        userRepository.save(user);

        logger.info("Utente {} (ID: {}) sbloccato.", user.getUsername(), user.getId());

        // Logica master: Annulla l'orfanezza delle campagne
        if (user.getMaster() != null) {

            logger.warn("L'utente sbloccato è un Master. Annullamento timer orfano per le sue campagne.");

            List<Campaign> campaignsToSave = campaignRepository.findByMasterId(user.getMaster().getId());

            for (Campaign campaign : campaignsToSave) {
                // ANNULLA il timer di cancellazione se era attivo
                campaign.setMasterBanPendingUntil(null);
                campaignRepository.save(campaign);

                logger.info("Campagna ID {} rimossa da 'pending delete'.",
                        campaign.getId());
            }
        }

        return ResponseEntity.ok(new MessageResponse(
                "Utente " + user.getUsername() + " sbloccato con successo."));
    }

    // NUOVO: Modifica i dettagli di un utente, inclusi i ruoli
    @Transactional
    public ResponseEntity<?> updateUserByAdmin(Long userId, AdminUserUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));

        // 1. Validazione di UNICITÀ (necessaria perché non c'è @Valid qui)
        if (!user.getUsername().equals(dto.getUsername()) && userRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("Errore: Username già in uso!");
        }
        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Errore: Email già in uso!");
        }

        // 2. Aggiornamento campi base
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setProfileImageUrl(dto.getProfileImageUrl());

        // 3. Aggiornamento Ruoli (Logica complessa)
        // Non modifichiamo i ruoli PLAYER/MASTER direttamente, ma solo
        // ROLE_ADMIN/ROLE_USER

        // a) Rimuovi tutti i ruoli statici attuali (ROLE_USER, ROLE_ADMIN)
        Set<Role> currentRoles = user.getRoles().stream()
                .filter(r -> !r.getRoleName().equals(RoleName.ROLE_USER)
                        && !r.getRoleName().equals(RoleName.ROLE_ADMIN))
                .collect(Collectors.toSet());

        // b) Aggiungi ROLE_USER (Deve esserci sempre, altrimenti fallisce
        // @PreAuthorize("hasRole('USER')"))
        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Ruolo ROLE_USER non trovato."));
        currentRoles.add(userRole);

        // c) Aggiungi ROLE_ADMIN se richiesto
        if (dto.isAdmin()) {
            Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Ruolo ROLE_ADMIN non trovato."));
            currentRoles.add(adminRole);
        }

        user.setRoles(currentRoles);

        // 4. Salva e ritorna
        userRepository.save(user);

        logger.info("Admin ha modificato l'utente ID {}", userId);
        return ResponseEntity.ok(new MessageResponse("Utente modificato con successo."));
    }

    // NUOVO: Promuove un utente a Admin
    @Transactional
    public ResponseEntity<?> promoteToAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + userId));

        Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Ruolo ROLE_ADMIN non trovato."));

        if (user.getRoles().contains(adminRole)) {
            return ResponseEntity.badRequest().body(new MessageResponse("L'utente è già un ADMIN."));
        }

        // Aggiungi ROLE_ADMIN
        user.getRoles().add(adminRole);
        userRepository.save(user);

        logger.warn("Utente ID {} promosso ad ADMIN.", userId);
        return ResponseEntity.ok(new MessageResponse("Utente promosso ad ADMIN con successo."));
    }
}