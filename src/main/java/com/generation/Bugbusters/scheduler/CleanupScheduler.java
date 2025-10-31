package com.generation.Bugbusters.scheduler;

import com.generation.Bugbusters.entity.Campaign;
import com.generation.Bugbusters.entity.User;
import com.generation.Bugbusters.repository.CampaignRepository;
import com.generation.Bugbusters.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CleanupScheduler.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    /**
     * esegue la pulizia delle campagne orfane da 30 giorni
     * gira una volta al giorno, 10 minuti dopo la mezzanotte
     */
    @Scheduled(cron = "0 10 0 * * ?") // (sec min ora gg mm sett)
    @Transactional 
    public void purgeOrphanedCampaigns() {
        logger.info("Esecuzione scheduler pulizia: Ricerca campagne orfane (30gg)...");
        LocalDateTime now = LocalDateTime.now();

        // trova campagne dove il timer di 30gg è scaduto
        List<Campaign> campaignsToDelete = 
                campaignRepository.findByMasterBanPendingUntilBefore(now);

        if (campaignsToDelete.isEmpty()) {
            logger.info("Nessuna campagna orfana da eliminare.");
            return;
        }

        logger.warn("Trovate {} campagne orfane. Inizio eliminazione...", 
                campaignsToDelete.size());

        // elimina ogni campagna
        for (Campaign campaign : campaignsToDelete) {
            logger.info("Eliminazione campagna ID: {}", campaign.getId());
            
            // grazie a @Transactional, se questo fallisce, tutto va in rollback
            campaignRepository.delete(campaign); 
        }
        logger.warn("Pulizia campagne orfane completata.");
    }

    /**
     * esegue la pulizia degli account bannati da 1 anno
     * gira una volta al giorno, 20 minuti dopo la mezzanotte.
     */
    @Scheduled(cron = "0 20 0 * * ?") // 10 minuti dopo l'altro scheduler
    @Transactional
    public void purgeBannedUsers() {
        logger.info("Esecuzione scheduler pulizia: Ricerca utenti bannati (1 anno)...");
        LocalDateTime now = LocalDateTime.now();

        // trova utenti bannati dove il timer di 1 anno è scaduto
        List<User> usersToDelete = 
                userRepository.findByIsBannedTrueAndDeletionScheduledOnBefore(now);

        if (usersToDelete.isEmpty()) {
            logger.info("Nessun utente bannato da eliminare.");
            return;
        }

        logger.warn("Trovati {} utenti bannati da 1 anno. Inizio eliminazione...", 
                usersToDelete.size());

        // elimina ogni utente
        for (User user : usersToDelete) {
            logger.info("Eliminazione utente ID: {} (Username: {})", 
                    user.getId(), user.getUsername());

            // grazie a CascadeType.ALL, eliminando l'utente eliminerà
            // anche i suoi profili, schede, voti, ecc.
            userRepository.delete(user);
        }
        logger.warn("Pulizia utenti bannati completata.");
    }
}