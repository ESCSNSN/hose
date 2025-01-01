package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "competition_like_table",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "free_entity_id"})}
)

public class CompetitionLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_entity_id", nullable = false)
    private CompetitionEntity competitionEntity;

    public static CompetitionLikeEntity toCompetitionLikeEntity(CompetitionEntity competitionEntity,String userId) {
        CompetitionLikeEntity competitionLikeEntity = new CompetitionLikeEntity();
        competitionLikeEntity.userId = userId;
        competitionLikeEntity.competitionEntity = competitionEntity;
        return competitionLikeEntity;
    }
}
