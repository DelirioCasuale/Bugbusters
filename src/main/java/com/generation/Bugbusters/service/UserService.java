package com.generation.Bugbusters.service;

import com.generation.Bugbusters.dto.JwtResponse;
import com.generation.Bugbusters.dto.MessageResponse;
import com.generation.Bugbusters.dto.UpdatePasswordRequest;
import com.generation.Bugbusters.dto.UpdateProfileRequest;
import com.generation.Bugbusters.entity.User;
import com.generation.Bugbusters.exception.BadRequestException;
import com.generation.Bugbusters.repository.UserRepository;
import com.generation.Bugbusters.security.JwtUtils;
import com.generation.Bugbusters.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    // Metodo helper per prendere l'utente loggato
    private User getAuthenticatedUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }

    // Metodo helper per generare un nuovo token (DRY)
    private ResponseEntity<JwtResponse> generateUpdatedTokenResponse(User user) {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String jwt = jwtUtils.generateJwtToken(authentication);
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                user.getProfileImageUrl()));
    }

    @Transactional
    public ResponseEntity<JwtResponse> updateProfile(UpdateProfileRequest request) {
        User user = getAuthenticatedUser();

        // 1. Controlla se il nuovo username è già in uso da QUALCUN ALTRO
        if (!Objects.equals(user.getUsername(), request.getNewUsername()) && 
            userRepository.existsByUsername(request.getNewUsername())) {
            throw new BadRequestException("Errore: Username già in uso!");
        }

        // 2. Controlla se la nuova email è già in uso da QUALCUN ALTRO
        if (!Objects.equals(user.getEmail(), request.getNewEmail()) && 
            userRepository.existsByEmail(request.getNewEmail())) {
            throw new BadRequestException("Errore: Email già in uso!");
        }

        // 3. Aggiorna i campi
        user.setUsername(request.getNewUsername());
        user.setEmail(request.getNewEmail());
        user.setProfileImageUrl(request.getNewImageUrl()); // Salva URL (anche se nullo o vuoto)
        
        User savedUser = userRepository.save(user);

        // 4. Restituisci un NUOVO token con i dati aggiornati
        return generateUpdatedTokenResponse(savedUser);
    }

    @Transactional
    public ResponseEntity<MessageResponse> updatePassword(UpdatePasswordRequest request) {
        User user = getAuthenticatedUser();

        // 1. Verifica la vecchia password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Errore: La vecchia password non è corretta.");
        }

        // 2. Aggiorna con la nuova password (hashata)
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Password aggiornata con successo!"));
    }
}