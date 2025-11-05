package com.generation.Bugbusters.security;

import com.generation.Bugbusters.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

// classe che ha il compito di avvolgere la entità user e la traduce per Spring Security
@Getter // meglio usare @Getter di Lombok e non @Data per evitare problemi con
        // equals/hashCode
// che potrebbero essere generati in modo non corretto a causa delle collezioni
// lazy-loaded
public class UserDetailsImpl implements UserDetails {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsImpl.class);

    private final Long id;
    private final String username;
    private final String email;
    private final boolean isBanned;

    // ATTENZIONE non bisogna salvare la password in chiaro
    // @JsonIgnore (sará effettivamente implementato quando useremo i DTO)
    private final String password; // questa sarà la password HASHATA

    // questa è la collezione di ruoli che Spring Security usa per le autorizzazioni
    private final Collection<? extends GrantedAuthority> authorities;

    // costruttore privato, serve a forzare l'uso del metodo build
    private UserDetailsImpl(Long id, String username, String email, String password,
            boolean isBanned, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isBanned = isBanned;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        logger.info("Building UserDetails for: {}", user.getUsername()); // Log Utente

        // 1. Prende i ruoli "statici" dal DB (ROLE_USER, ROLE_ADMIN)
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toSet());
        logger.info("Static roles from DB for {}: {}", user.getUsername(),
                authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())); // Log Ruoli
                                                                                                        // Statici

        // 2. Aggiunge i ruoli "dinamici" basati sui profili
        // N.B: Funziona perché il metodo che carica l'User è @Transactional
        try {
            if (user.getPlayer() != null) {
                logger.info("Player profile FOUND for {}. Adding ROLE_PLAYER.", user.getUsername()); // Log Successo
                                                                                                     // Player
                authorities.add(new SimpleGrantedAuthority("ROLE_PLAYER"));
            } else {
                logger.warn("Player profile NOT found for {}.", user.getUsername()); // Log Fallimento Player
            }
        } catch (Exception e) {
            logger.error("Error accessing player profile for {}: {}", user.getUsername(), e.getMessage()); // Log Errore
                                                                                                           // Accesso
        }

        try {
            if (user.getMaster() != null) {
                logger.info("Master profile FOUND for {}. Adding ROLE_MASTER.", user.getUsername()); // Log Successo
                                                                                                     // Master
                authorities.add(new SimpleGrantedAuthority("ROLE_MASTER"));
            } else {
                logger.warn("Master profile NOT found for {}.", user.getUsername()); // Log Fallimento Master
            }
        } catch (Exception e) {
            logger.error("Error accessing master profile for {}: {}", user.getUsername(), e.getMessage()); // Log Errore
                                                                                                           // Accesso
        }

        logger.info("Final authorities for {}: {}", user.getUsername(),
                authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())); // Log Ruoli
                                                                                                        // Finali

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.isBanned(),
                authorities // Passiamo il set completo di autorità
        );
    }

    // metodi necessari dell'interfaccia UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // non gestisco la scadenza dell'account
    }

    @Override
    public boolean isAccountNonLocked() {
        // Se isBanned è true, l'account è considerato locked
        return !isBanned;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // non gestisco la scadenza credenziali
    }

    @Override
    public boolean isEnabled() {
        return true; // gestisco l'abilitazione tramite isBanned
    }
}