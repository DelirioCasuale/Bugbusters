package com.generation.Bugbusters.entity;

import java.sql.Date;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class CampaignSession {

    private Long id;
    private Campaign campaign;
    private Date session_date;
    private String summary;

}
