package com.generation.Bugbusters.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration // serve per dire a Spring che questa è una classe di configurazione
public class PasswordEncoderConfig {

    @Bean // rende questo oggetto disponibile per l'iniezione delle dipendenze
    public PasswordEncoder passwordEncoder() {
        // userei BCrypt, lo standard de-facto per l'hashing delle password, con un buon livello di sicurezza ma anche di performance
        // senza complicazioni inutili
        return new BCryptPasswordEncoder();
    }
}