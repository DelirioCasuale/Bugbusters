package com.generation.Bugbusters.config;

import com.generation.Bugbusters.security.AuthTokenFilter;
// import com.generation.Bugbusters.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity // abilita la configurazione di sicurezza web
@EnableMethodSecurity // abilita la sicurezza a livello di metodo
public class SecurityConfig {

    // @Autowired
    // private UserDetailsServiceImpl userDetailsService;

    // @Autowired
    // private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthTokenFilter authTokenFilter; // inietta il filtro JWT

    /**
     * Questo Bean definisce il "provider" di autenticazione.
     * Collega il servizio che carica gli utenti (UserDetailsService) con l'encoder
     * che controlla le password (PasswordEncoder).
     */
    // @Bean
    // public AuthenticationProvider authenticationProvider() {
    // DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    // authProvider.setUserDetailsService(userDetailsService);
    // authProvider.setPasswordEncoder(passwordEncoder);
    // return authProvider;
    // }

    /**
     * Espone l'AuthenticationManager come Bean, necessario per autenticare
     * manualmente gli utenti nel controller di login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // BEAN corsFilter() c'é gia definito piú sotto ma non è necessario perché siamo
    // sulla stessa origine (localhost:8080)
    /**
     * Configurazione base per il CORS (Cross-Origin Resource Sharing).
     * FONDAMENTALE per permettere al frontend di chiamare il backend senza problemi
     * di politica di stessa origine.
     */
    // @Bean
    // public CorsFilter corsFilter() { // configurazione del filtro CORS in caso
    // serva se si dovessero usare React, Angular, ecc.
    // UrlBasedCorsConfigurationSource source = new
    // UrlBasedCorsConfigurationSource();
    // CorsConfiguration config = new CorsConfiguration();
    // config.setAllowCredentials(true);
    // config.addAllowedOrigin("http://localhost:3000"); // URL per il frontend,
    // serve a permettere le richieste da lì
    // config.addAllowedHeader("*");
    // config.addAllowedMethod("*");
    // source.registerCorsConfiguration("/**", config);
    // return new CorsFilter(source);
    // }

    /**
     * Questo è il "collo di bottiglia" della sicurezza.
     * Definiamo la catena di filtri e le regole di accesso.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(authz -> authz
                        // Endpoint Pubblici API
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()

                        // TUTTE le pagine HTML sono ora pubbliche (protezione lato client)
                        .requestMatchers("/", "/*.html", "/css/**", "/js/**", "/images/**", "/error", "/favicon.ico")
                        .permitAll()

                        // Controller routes - redirect pubblici
                        .requestMatchers("/landing", "/signup", "/dashboard/**", "/admin", "/profile").permitAll()

                        // Solo gli Endpoint API restano protetti (qui serve il JWT)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/profile/**").hasAnyRole("USER", "PLAYER", "MASTER", "ADMIN")
                        .requestMatchers("/api/player/**").hasRole("PLAYER")
                        .requestMatchers("/api/master/**").hasRole("MASTER")

                        // Altre richieste API richiedono autenticazione
                        .requestMatchers("/api/**").authenticated()

                        // Tutto il resto pubblico
                        .anyRequest().permitAll());

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}