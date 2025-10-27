package com.generation.Bugbusters.entity;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name="campaigns")
@EqualsAndHashCode
public class Campaign {

    private Long id;
    private String title;
    private String description;
    private Date start_date;
    private Date scheduled_next_session;
    private String invite_players_code;
    private String invite_masters_code;

@OneToMany(mappedBy = "campaign")
@JsonIgnore
private List<CampaignSession> campaign_sessions;

@ManyToMany(mappedBy = "campaigns")
@JsonIgnore
private List<CharacterSheet> character_sheets;

@ManyToOne
@JoinColumn(
    name = "master_id",
    referencedColumnName = "id"
)
private Master master;

}
