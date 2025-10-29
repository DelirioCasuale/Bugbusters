package com.generation.Bugbusters.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * eccezione per quando una richiesta è semanticamente errata
 * (es. provare a rimuovere un player che non è nella campagna)
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}