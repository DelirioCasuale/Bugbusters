package com.generation.Bugbusters.entity;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class CharacterSheet {
    
    private Long id;
    private User user_id; // riferimento alla tua entità utenti
    private String nome_file;
    private Integer versione = 1;
    private String tipo_mime;
    private byte[] contenuto;
    private Date data_caricamento = Date.valueOf(LocalDate.now());
    private List<Campaign> campaigns;
}
