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
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name="character_sheet")
@EqualsAndHashCode
public class CharacterSheet {
    
    private Long id;
    @ManyToOne
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "id"
    )
    private Player player_id; // riferimento alla tua entità utenti
    private String nome_file;
    private Integer versione = 1;
    private String tipo_mime;
    @Blob
    private byte[] contenuto;
    private Date data_caricamento = Date.valueOf(LocalDate.now());

    @ManyToMany
    @JoinTable(
        name=" campaign_players",
        joinColumns =
        {@JoinColumn (name="character_id")},
        inverseJoinColumns=
        {@JoinColumn (name=" campaign_id")}
    )
    private List<Campaign> campaigns;
}
