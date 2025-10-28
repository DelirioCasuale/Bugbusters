package com.generation.Bugbusters.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component // serve per rendere questa classe un Bean gestito da Spring
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // inietta i valori da application.properties
    @Value("${bugbusters.app.jwtSecret}")
    private String jwtSecret;

    @Value("${bugbusters.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Genera un token JWT per l'utente autenticato.
     */
    public String generateJwtToken(Authentication authentication) {
        
        // prende i dettagli dell'utente (il nostro UserDetailsImpl)
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // costruisce il token
        return Jwts.builder()
                .subject(userPrincipal.getUsername()) // il soggetto è l'username
                .issuedAt(now) // data di creazione
                .expiration(expiryDate) // data di scadenza
                .signWith(getKey()) // firma il token con la nostra chiave segreta
                .compact(); // costruisce la stringa
    }

    /**
     * Estrae l'username dal token (se valido).
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Valida la firma e la scadenza del token.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT non valido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT scaduto: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT non supportato: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Payload del JWT vuoto: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Errore generico validazione token: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Helper per generare la chiave segreta (SecretKey) 
     * a partire dalla stringa in properties.
     */
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}