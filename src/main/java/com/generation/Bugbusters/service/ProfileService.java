package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.JwtResponse;
import com.generation.Bugbusters.dto.MessageResponse;
import com.generation.Bugbusters.entity.Master;
import com.generation.Bugbusters.entity.Player;
import com.generation.Bugbusters.entity.User;
import com.generation.Bugbusters.repository.MasterRepository;
import com.generation.Bugbusters.repository.PlayerRepository;
import com.generation.Bugbusters.repository.UserRepository;
import com.generation.Bugbusters.security.JwtUtils;
import com.generation.Bugbusters.security.UserDetailsImpl;

import jakarta.persistence.EntityManager; // <-- IMPORTA QUESTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private MasterRepository masterRepository;
    @Autowired
    private JwtUtils jwtUtils;
    
    // --- AGGIUNGI QUESTO ---
    @Autowired
    private EntityManager entityManager;

    /**
     * Rende l'utente loggato un "Player" e restituisce un nuovo token.
     */
    @Transactional
    public ResponseEntity<?> becomePlayer() {
        User currentUser = getCurrentUser();

        if (playerRepository.existsById(currentUser.getId())) {
            return new ResponseEntity<>(
                new MessageResponse("Errore: Sei già un Player!"), 
                HttpStatus.BAD_REQUEST);
        }

        Player newPlayer = new Player();
        newPlayer.setUser(currentUser);
        playerRepository.save(newPlayer);
        
        // --- LOGICA DI REFRESH CORRETTA ---
        // Forza il "flush" delle modifiche al DB (l'insert del player)
        entityManager.flush(); 
        // "Pulisce" la cache di primo livello di Hibernate
        entityManager.clear(); 
        
        // Ora, ricaricando l'utente, siamo sicuri di prenderlo
        // dal DB e non dalla cache, e vedrà le nuove relazioni.
        User updatedUser = userRepository.findById(currentUser.getId()).get();

        return generateUpdatedTokenResponse(updatedUser);
    }

    /**
     * Rende l'utente loggato un "Master" e restituisce un nuovo token.
     */
    @Transactional
    public ResponseEntity<?> becomeMaster() {
        User currentUser = getCurrentUser();

        if (masterRepository.existsById(currentUser.getId())) {
             return new ResponseEntity<>(
                new MessageResponse("Errore: Sei già un Master!"), 
                HttpStatus.BAD_REQUEST);
        }

        Master newMaster = new Master();
        newMaster.setUser(currentUser);
        masterRepository.save(newMaster);

        // --- LOGICA DI REFRESH CORRETTA ---
        entityManager.flush();
        entityManager.clear();
        User updatedUser = userRepository.findById(currentUser.getId()).get();

        return generateUpdatedTokenResponse(updatedUser);
    }

    /**
     * Metodo helper (DRY) per ottenere l'utente dalla sessione
     */
    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }
    
    /**
     * NUOVO METODO HELPER (DRY)
     * Genera un nuovo JwtResponse per un utente aggiornato.
     */
    private ResponseEntity<JwtResponse> generateUpdatedTokenResponse(User user) {
        // 1. Costruisci il nuovo UserDetails con i ruoli aggiornati (es. ROLE_PLAYER)
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        // 2. Crea un nuovo oggetto Authentication per il generatore di token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, 
                null, // Non servono credenziali, è già autenticato
                userDetails.getAuthorities());
        
        // 3. Genera un nuovo token JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. Estrai i ruoli come stringhe
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 5. Restituisci il nuovo JwtResponse
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                user.getProfileImageUrl()));
    }
}