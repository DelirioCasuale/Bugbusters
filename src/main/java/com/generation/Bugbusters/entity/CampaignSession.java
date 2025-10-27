package com.generation.Bugbusters.entity;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name="campaign_sessions")
@EqualsAndHashCode
public class CampaignSession {

    private Long id;
    private Campaign campaign;
    private Date session_date;
    private String summary;

}
