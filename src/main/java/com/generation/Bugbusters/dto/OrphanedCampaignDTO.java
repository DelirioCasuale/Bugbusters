package com.generation.Bugbusters.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO per mostrare a un player una campagna che ha perso il suo master (bannato)
 * ed è in attesa di un sostituto
 */
@Data
public class OrphanedCampaignDTO {
    
    private Long campaignId;
    private String campaignTitle;

    // il codice da dare a un nuovo master per fargli reclamare la campagna
    private String inviteMastersCode;

    
    // la data di scadenza entro cui un nuovo master deve
    // essere invitato prima che la campagna venga eliminata
    private LocalDateTime deletionDeadline; // corrisponde a 'masterBanPendingUntil'
}