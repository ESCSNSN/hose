package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(
        name = "competition_scrap_table",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "competition_entity_id"})}
)
public class CompetitionScrapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comptition_entity_id", nullable = false)
    private CompetitionEntity competitionEntity;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    public static CompetitionScrapEntity toScrapEntity(CompetitionEntity competitionEntity, String userId) {
        CompetitionScrapEntity competitionScrapEntity = new CompetitionScrapEntity();
        competitionScrapEntity.setUserId(userId);
        competitionScrapEntity.setCompetitionEntity(competitionEntity);
        return competitionScrapEntity;
    }
}
