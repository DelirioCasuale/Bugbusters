package com.generation.Bugbusters.repository;

import com.generation.Bugbusters.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository // specifica che questa è un'interfaccia Repository gestita da Spring
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    // ci fornisce automaticamente: findById, save, delete, findAll, etc.
    // possiamo aggiungere metodi personalizzati se necessario o usare query derivati (SPERO DI NON AVERNE BISOGNO)

    // per la logica degli inviti
    /**
     * Trova una campagna tramite il codice di invito per i giocatori.
     */
    Optional<Campaign> findByInvitePlayersCode(String code);

    /**
     * Trova una campagna tramite il codice di invito per i master.
     */
    Optional<Campaign> findByInviteMastersCode(String code);

    // per la logica di ban (scheduler)
    /**
     * Trova tutte le campagne orfane la cui data di scadenza per trovare un nuovo master è passata.
     */
    List<Campaign> findByMasterBanPendingUntilBefore(LocalDateTime now);
}