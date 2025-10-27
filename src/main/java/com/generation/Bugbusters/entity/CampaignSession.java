package com.generation.Bugbusters.entity;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name="campaign_sessions")
@EqualsAndHashCode
public class CampaignSession {

    private Long id;
    private Date session_date;
    private String summary;

@ManyToOne
@JoinColumn (
name = "campaign_id",
referencedColumnName = "id"
)
private Campaign campaign;

}
