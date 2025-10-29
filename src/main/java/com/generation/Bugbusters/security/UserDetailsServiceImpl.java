package com.generation.Bugbusters.security;

import com.generation.Bugbusters.entity.User;
import com.generation.Bugbusters.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // cice a Spring che questa è una classe Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired // inietta il repository degli user
    private UserRepository userRepository;

    /**
     * Questo è l'UNICO metodo che Spring Security chiama quando un utente cerca di autenticarsi.
     */
    @Override
    @Transactional // Manteniamo @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. Cerca l'utente nel DB
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utente non trovato con username: " + username));

        // --- AGGIUNTA ESPLICITA (se necessario) ---
        // Forza l'inizializzazione dei proxy LAZY dentro la transazione
        // Questo a volte è necessario se il proxy non viene "toccato"
        // abbastanza presto da Hibernate.
        // Prova *prima* senza queste righe, poi aggiungile se i log
        // in UserDetailsImpl indicano ancora "NOT found".
         try {
             if (user.getPlayer() != null) {
                 user.getPlayer().getId(); // Tocca l'entità Player per inizializzarla
             }
             if (user.getMaster() != null) {
                 user.getMaster().getId(); // Tocca l'entità Master per inizializzarla
             }
         } catch (Exception e) {
              // Logga eventuali errori durante l'inizializzazione forzata
              System.err.println("WARN: Could not eagerly initialize profiles for user " + username + ": " + e.getMessage());
         }


        // 2. "Traduci" l'entità User in un UserDetails (ora i profili DOVREBBERO essere caricati)
        return UserDetailsImpl.build(user);
    }
}