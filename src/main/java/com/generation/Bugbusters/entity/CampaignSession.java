package com.generation.Bugbusters.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "campaign_sessions")
@Data
@NoArgsConstructor
public class CampaignSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(columnDefinition = "TEXT")
    private String summary;

    
    // relazioni

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;
}