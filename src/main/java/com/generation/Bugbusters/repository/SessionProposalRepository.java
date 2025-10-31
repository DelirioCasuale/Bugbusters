package com.generation.Bugbusters.repository;

import com.generation.Bugbusters.entity.SessionProposal;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // specifica che questa è un'interfaccia Repository gestita da Spring
public interface SessionProposalRepository extends JpaRepository<SessionProposal, Long> {
    // ci fornisce automaticamente: findById, save, delete, findAll, etc.
    // possiamo aggiungere metodi personalizzati se necessario o usare query derivati (SPERO DI NON AVERNE BISOGNO)

    /**
     * Trova tutte le proposte per una campagna che non sono ancora scadute e non sono ancora state confermate
     */
    List<SessionProposal> findByCampaignIdAndExpiresOnAfterAndIsConfirmedFalse(
            Long campaignId, 
            LocalDateTime now);
    
    /**
     * trova tutte le proposte la cui data di scadenza (expiresOn)
     * è passata (è 'before' now) e che non sono ancora state confermate
     */
    List<SessionProposal> findByExpiresOnBeforeAndIsConfirmedFalse(LocalDateTime now);
}