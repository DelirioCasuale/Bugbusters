package com.generation.Bugbusters.scheduler;

import com.generation.Bugbusters.entity.Campaign;
import com.generation.Bugbusters.entity.SessionProposal;
import com.generation.Bugbusters.repository.CampaignRepository;
import com.generation.Bugbusters.repository.SessionProposalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component // dice a Spring di creare e gestire questa classe 
public class SessionScheduler {

    // logger per monitorare l'attività dello scheduler
    private static final Logger logger = LoggerFactory.getLogger(SessionScheduler.class);

    @Autowired
    private SessionProposalRepository sessionProposalRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    /**
     * questo metodo verrà eseguito automaticamente ogni 15 minuti.
     * (15 * 60 * 1000 = 900000 millisecondi)
     */
    @Scheduled(fixedRate = 900000) 
    @Transactional
    public void autoConfirmExpiredProposals() {
        
        logger.info("Esecuzione scheduler: Conferma proposte scadute...");
        LocalDateTime now = LocalDateTime.now();

        // trova tutte le proposte scadute e non confermate
        List<SessionProposal> expiredProposals = 
                sessionProposalRepository.findByExpiresOnBeforeAndIsConfirmedFalse(now);

        if (expiredProposals.isEmpty()) {
            logger.info("Nessuna proposta da confermare.");
            return;
        }

        logger.info("Trovate {} proposte da confermare automaticamente.", expiredProposals.size());

        // itera su ognuna e confermala
        for (SessionProposal proposal : expiredProposals) {

            // conferma la proposta
            proposal.setConfirmed(true);
            sessionProposalRepository.save(proposal);

            // aggiorna la data della campagna
            Campaign campaign = proposal.getCampaign();
            
            // converte LocalDateTime in LocalDate per il campo della campagna
            LocalDate sessionDate = proposal.getProposedDate().toLocalDate();
            
            campaign.setScheduledNextSession(sessionDate);
            campaignRepository.save(campaign);
            
            logger.info("Confermata proposta {} per campagna {}", 
                    proposal.getId(), campaign.getId());
        }
    }
}