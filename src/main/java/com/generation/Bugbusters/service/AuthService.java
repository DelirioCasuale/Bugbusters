package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.JwtResponse;
import com.generation.Bugbusters.dto.LoginRequest;
import com.generation.Bugbusters.dto.MessageResponse;
import com.generation.Bugbusters.dto.SignupRequest;
import com.generation.Bugbusters.entity.Role;
import com.generation.Bugbusters.entity.User;
import com.generation.Bugbusters.enumeration.RoleName;
import com.generation.Bugbusters.repository.RoleRepository;
import com.generation.Bugbusters.repository.UserRepository;
import com.generation.Bugbusters.security.JwtUtils;
import com.generation.Bugbusters.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    // iniettiamo tutti gli strumenti che ci servono
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    // logica per autenticare un utente
    public JwtResponse authenticateUser(LoginRequest loginRequest) {

        // usa l'AuthenticationManager di Spring per validare utente/password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        // se l'autenticazione ha successo, la imposta nel contesto di sicurezza
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // genera il token JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // estrae i dettagli dell'utente per la risposta
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // ritorna il DTO di risposta con il token e i dati utente
        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    // Logica per registrare un nuovo utente.
    public MessageResponse registerUser(SignupRequest signUpRequest) {

        // controlla se l'username è già in uso
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            // Sarebbe meglio gestire questa situazione con eccezioni personalizzate
            return new MessageResponse("Errore: Username già in uso!");
        }

        // controlla se l'email è già in uso
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new MessageResponse("Errore: Email già in uso!");
        }

        // crea il nuovo utente
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());

        // codifica la password prima di salvarla
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        // assegna il ruolo base (ROLE_USER)
        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Errore: Ruolo ROLE_USER non trovato."));
        user.setRoles(Collections.singleton(userRole)); // singleton perché un solo ruolo

        // salva l'utente nel database
        userRepository.save(user);

        return new MessageResponse("Utente registrato con successo!");
    }
}