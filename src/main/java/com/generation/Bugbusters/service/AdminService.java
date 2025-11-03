package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.AdminUserViewDTO;
import com.generation.Bugbusters.dto.MessageResponse;
import com.generation.Bugbusters.entity.Campaign;
import com.generation.Bugbusters.entity.User;
import com.generation.Bugbusters.exception.ResourceNotFoundException;
import com.generation.Bugbusters.mapper.UserMapper;
import com.generation.Bugbusters.repository.CampaignRepository;
import com.generation.Bugbusters.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    // logger per il logging delle operazioni serve per monitorare le azioni amministrative
    // (es. ban utenti, modifiche critiche, ecc)
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper; // iniettiamo il mapper per convertire User in AdminUserViewDTO

    @Autowired
    private CampaignRepository campaignRepository; 

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
            List<Campaign> campaignsToOrphan = 
                    campaignRepository.findByMasterId(user.getMaster().getId());

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
}