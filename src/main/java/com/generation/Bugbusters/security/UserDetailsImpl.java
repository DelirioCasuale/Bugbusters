package com.generation.Bugbusters.security;

import com.generation.Bugbusters.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

// classe che ha il compito di avvolgere la entità user e la traduce per Spring Security
@Getter // meglio usare @Getter di Lombok e non @Data per evitare problemi con equals/hashCode
// che potrebbero essere generati in modo non corretto a causa delle collezioni lazy-loaded
public class UserDetailsImpl implements UserDetails {

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

    
    
    // metodo factory MODIFICATO per includere ruoli dinamici
    public static UserDetailsImpl build(User user) {
        
        // prende i ruoli statici dal DB (ROLE_USER, ROLE_ADMIN)
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toSet()); // raccoglie in un set

        // aggiunge i ruoli dinamici basati sui profili
        // N.B: funziona perché il metodo che carica l'User è @Transactional
        if (user.getPlayer() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_PLAYER"));
        }
        if (user.getMaster() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_MASTER"));
        }

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.isBanned(),
                authorities // bisogna passare il set completo di autorità
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
        // serve affinché un utente bannato sia considerato locked
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