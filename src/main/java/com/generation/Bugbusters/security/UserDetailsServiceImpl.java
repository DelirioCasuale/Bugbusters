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
    @Transactional // serve a caricare le relazioni (i ruoli) ed evitare problemi di LazyInitializationException e simili
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // cerca l'utente nel db usando il repository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utente non trovato con username: " + username));

        // traduce l'entità user in un UserDetails
        return UserDetailsImpl.build(user);
    }
}