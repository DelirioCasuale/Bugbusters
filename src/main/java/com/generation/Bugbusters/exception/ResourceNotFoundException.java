package com.generation.Bugbusters.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Eccezione per quando una risorsa (es. Campagna) non viene trovata.
 * (Verrà gestita dal nostro service, o da un futuro handler)
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Opzionale, ma utile
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}