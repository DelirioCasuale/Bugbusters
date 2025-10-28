package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.MessageResponse;
import com.generation.Bugbusters.entity.Master;
import com.generation.Bugbusters.entity.Player;
import com.generation.Bugbusters.entity.User;
import com.generation.Bugbusters.repository.MasterRepository;
import com.generation.Bugbusters.repository.PlayerRepository;
import com.generation.Bugbusters.repository.UserRepository;
import com.generation.Bugbusters.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MasterRepository masterRepository;

    
    // rende l'utente attualmente loggato un "player"
    @Transactional // assicura che tutte le operazioni db in questo metodo siano atomiche, cioè si completano tutte o nessuna
    public MessageResponse becomePlayer() {
        // ottiene l'utente loggato dal contesto di sicurezza
        User currentUser = getCurrentUser();

        // controlla se è già un player
        // (usiamo existsById perché l'id del player è lo stesso dell'user)
        if (playerRepository.existsById(currentUser.getId())) {
            return new MessageResponse("Errore: Sei già un Player!");
        }

        // crea e salva il nuovo profilo player
        Player newPlayer = new Player();
        newPlayer.setUser(currentUser); // imposta la relazione @MapsId
        playerRepository.save(newPlayer);

        return new MessageResponse("Sei diventato un Player! Fai di nuovo il login per aggiornare i tuoi ruoli.");
    }

    
    // rende l'utente attualmente loggato un master
    @Transactional
    public MessageResponse becomeMaster() {
        // ottiene l'utente loggato
        User currentUser = getCurrentUser();

        // controlla se è già un master
        if (masterRepository.existsById(currentUser.getId())) {
            return new MessageResponse("Errore: Sei già un Master!");
        }

        // crea e salva il nuovo profilo master
        Master newMaster = new Master();
        newMaster.setUser(currentUser); // imposta la relazione @MapsId
        masterRepository.save(newMaster);

        return new MessageResponse("Sei diventato un Master! Fai di nuovo il login per aggiornare i tuoi ruoli.");
    }

    // metodo helper per ottenere l'utente dalla sessione
    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        
        // va ricaricato l'utente dal repository per assicurasi
        // che sia un'entità gestita da JPA nel contesto di questa richiesta
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }
}