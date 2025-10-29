package com.generation.Bugbusters.config;

import com.generation.Bugbusters.security.AuthTokenFilter;
import com.generation.Bugbusters.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // abilita la configurazione di sicurezza web
@EnableMethodSecurity // abilita la sicurezza a livello di metodo
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthTokenFilter authTokenFilter; // inietta il filtro JWT

    /**
     * Questo Bean definisce il "provider" di autenticazione.
     * Collega il servizio che carica gli utenti (UserDetailsService) con l'encoder che controlla le password (PasswordEncoder).
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Espone l'AuthenticationManager come Bean, necessario per autenticare manualmente gli utenti nel controller di login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // BEAN corsFilter() c'é gia definito piú sotto ma non è necessario perché siamo sulla stessa origine (localhost:8080)
    /**
     * Configurazione base per il CORS (Cross-Origin Resource Sharing).
     * FONDAMENTALE per permettere al frontend di chiamare il backend senza problemi di politica di stessa origine.
     */
    // @Bean
    // public CorsFilter corsFilter() { // configurazione del filtro CORS in caso serva se si dovessero usare React, Angular, ecc.
    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     CorsConfiguration config = new CorsConfiguration();
    //     config.setAllowCredentials(true);
    //     config.addAllowedOrigin("http://localhost:3000"); // URL per il frontend, serve a permettere le richieste da lì
    //     config.addAllowedHeader("*");
    //     config.addAllowedMethod("*");
    //     source.registerCorsConfiguration("/**", config);
    //     return new CorsFilter(source);
    // }

    /**
     * Questo è il "collo di bottiglia" della sicurezza.
     * Definiamo la catena di filtri e le regole di accesso.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // in caso di CORS sarebbe necessario abilitare il CORS qui
            .csrf(csrf -> csrf.disable()) // disabilita CSRF che sta per Cross-Site Request Forgery (non serve per API stateless, in parole semplici non vogliamo che il browser invii automaticamente i cookie di sessione)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // NON creare sessioni
            .authenticationProvider(authenticationProvider()) // usa il proprio provider
            .authorizeHttpRequests(authz -> authz
                // si definiscono gli endpoint PUBBLICI
                .requestMatchers("/api/auth/**").permitAll() // Es. /api/auth/login, /api/auth/register
                
                // PER IL TEST BASE IN /static
                // bisogna permettere l'accesso ai file statici (index.html, .js, .css)
                .requestMatchers("/", "/landing.html", "/register.html", "/css/**", "/js/**", "/images/**").permitAll() // permetti l'accesso alle risorse statiche PORCA PUTTANA
                
                // definiamo gli endpoint PROTETTI
                // endpoint admin
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // serve a creare i profili
                .requestMatchers("/api/profile/**").hasRole("USER")
                // solo chi é un player può accedere alle API da player
                .requestMatchers("/api/player/**").hasRole("PLAYER")
                // solo chi é un master può accedere alle API da master
                .requestMatchers("/api/master/**").hasRole("MASTER")
                
                .requestMatchers("/dashboard.html").hasRole("USER") // Richiede almeno il ruolo base
                .requestMatchers("/admin.html").hasRole("ADMIN") // Richiede il ruolo admin
                
                // qualsiasi altra richiesta deve essere autenticata
                .anyRequest().authenticated()
            );

        // aggiunta chiave di filtro JWT
        // aggiunge il filtro JWT prima del filtro standard di username/password
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}