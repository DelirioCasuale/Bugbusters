package com.generation.Bugbusters.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // serve che sia un Bean, così possiamo aggiungerlo alla SecurityConfig
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * Questo metodo viene eseguito per OGNI richiesta.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
                                    throws ServletException, IOException {
        try {
            // estrae il token dall'header
            String jwt = parseJwt(request);

            // se il token esiste ed è valido...
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                // estrae l'username
                String username = jwtUtils.getUsernameFromJwtToken(jwt);

                // carica l'utente dal DB
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // autentica l'utente per questa richiesta
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, // non servono credenziali in quanto stiamo usando un token
                                userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // imposta l'autenticazione per il contesto di sicurezza
                // questo è l'equivalente di loggare l'utente
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Impossibile impostare l'autenticazione utente: {}", e.getMessage());
        }

        // passa la richiesta al filtro successivo
        filterChain.doFilter(request, response);
    }

    /**
     * Helper per estrarre il token dall'header "Authorization: Bearer <token>"
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // rimuove Bearer
        }

        return null;
    }
}