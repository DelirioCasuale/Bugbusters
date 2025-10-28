package com.generation.Bugbusters.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// una risposta generica (es. per la registrazione o il logout)
@Data
@AllArgsConstructor
public class MessageResponse {
    private String message;
}