package com.generation.Bugbusters.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

// DTO per proporre una nuova data/ora di sessione.
@Data
public class SessionProposalRequest {

    @NotNull(message = "La data è obbligatoria")
    @Future(message = "La data deve essere nel futuro")
    private LocalDateTime proposedDate;
}