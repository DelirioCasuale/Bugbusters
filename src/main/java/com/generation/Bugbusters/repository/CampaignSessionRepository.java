package com.generation.Bugbusters.repository;

import com.generation.Bugbusters.entity.CampaignSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // specifica che questa è un'interfaccia Repository gestita da Spring
public interface CampaignSessionRepository extends JpaRepository<CampaignSession, Long> {
    // ci fornisce automaticamente: findById, save, delete, findAll, etc.
    // possiamo aggiungere metodi personalizzati se necessario o usare query derivati (SPERO DI NON AVERNE BISOGNO)
}