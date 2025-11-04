package com.generation.Bugbusters.repository;

import com.generation.Bugbusters.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository // specifica che questa è un'interfaccia Repository gestita da Spring
public interface UserRepository extends JpaRepository<User, Long> {

    // ci fornisce automaticamente: findById, save, delete, findAll, etc.
    // possiamo aggiungere metodi personalizzati se necessario o usare query
    // derivati (SPERO DI NON AVERNE BISOGNO)

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

    // per la logica di ban (scheduler che cancella gli utenti bannati dopo tot
    // giorni)

    /**
     * Trova tutti gli utenti bannati la cui data di cancellazione è passata.
     */
    List<User> findByIsBannedTrueAndDeletionScheduledOnBefore(LocalDateTime now);

    /**
     * trova tutti gli utenti che hanno un profilo player
     * spring capisce "PlayerIsNotNull" e lo traduce in SQL.
     */
    Page<User> findByPlayerIsNotNull(Pageable pageable);

    /**
     * trova tutti gli utenti che hanno un profilo master
     * spring capisce "MasterIsNotNull" e lo traduce in SQL.
     */
    Page<User> findByMasterIsNotNull(Pageable pageable);

    // NUOVO: Cerca TUTTI gli utenti per username o email
    @Query("SELECT u FROM User u WHERE (u.username LIKE %:search% OR u.email LIKE %:search%)")
    Page<User> findByUsernameContainingOrEmailContaining(@Param("search") String search, Pageable pageable);

    // NUOVO: Cerca SOLO PLAYER per username o email
    @Query("SELECT u FROM User u WHERE u.player IS NOT NULL AND (u.username LIKE %:search% OR u.email LIKE %:search%)")
    Page<User> findPlayersBySearch(@Param("search") String search, Pageable pageable);

    // NUOVO: Cerca SOLO MASTER per username o email
    @Query("SELECT u FROM User u WHERE u.master IS NOT NULL AND (u.username LIKE %:search% OR u.email LIKE %:search%)")
    Page<User> findMastersBySearch(@Param("search") String search, Pageable pageable);
}