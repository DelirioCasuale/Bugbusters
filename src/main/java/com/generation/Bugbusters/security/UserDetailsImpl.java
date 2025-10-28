package com.generation.Bugbusters.security;

import com.generation.Bugbusters.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
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

    
    // un metodo factory statico per creare un UserDetailsImpl a partire dalla nostra entità User.
    public static UserDetailsImpl build(User user) {
        // cosí ora si converte il Set<Role> in una List<SimpleGrantedAuthority>
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.isBanned(),
                authorities
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