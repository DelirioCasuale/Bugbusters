package com.generation.Bugbusters.entity;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name="campaigns")
@EqualsAndHashCode
public class Campaign {

    private Long id;
    private Master master;
    private String title;
    private String description;
    private Date start_date;
    private Date scheduled_next_session;
    private String invite_players_code;
    private String invite_masters_code;
    private List<CampaignSession> campaign_sessions;

}
