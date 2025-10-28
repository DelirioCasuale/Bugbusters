package com.generation.Bugbusters.repository;

import com.generation.Bugbusters.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository // specifica che questa è un'interfaccia Repository gestita da Spring
public interface UserRepository extends JpaRepository<User, Long> {

    // ci fornisce automaticamente: findById, save, delete, findAll, etc.
    // possiamo aggiungere metodi personalizzati se necessario o usare query derivati (SPERO DI NON AVERNE BISOGNO)

    
    // per sta cazzo di Spring Security e Login
    
    /**
     * Trova un utente dal suo username. 
     * Optional gestisce il caso in cui l'utente non esista.
     */
    Optional<User> findByUsername(String username);

    // per la Registrazione

    /**
     * Controlla se un utente esiste già con questo username.
     * È più performante di findByUsername.
     */
    Boolean existsByUsername(String username);

    /**
     * Controlla se un utente esiste già con questa email.
     */
    Boolean existsByEmail(String email);
    
    // per la logica di ban (scheduler che cancella gli utenti bannati dopo tot giorni)
    
    /**
     * Trova tutti gli utenti bannati la cui data di cancellazione è passata.
     */
    List<User> findByIsBannedTrueAndDeletionScheduledOnBefore(LocalDateTime now);
}