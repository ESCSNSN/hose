package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "competition_file_table")
public class CompetitionFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String originalFilename;

    @Column
    private String storedFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private CompetitionEntity competitionEntity;

    public static CompetitionFileEntity toCompetitionFileEntity(CompetitionEntity competitionEntity, String originalFilename, String storedFilename) {
        CompetitionFileEntity competitionFileEntity = new CompetitionFileEntity();
        competitionFileEntity.setOriginalFilename(originalFilename);
        competitionFileEntity.setStoredFilename(storedFilename);
        competitionFileEntity.setCompetitionEntity(competitionEntity);
        return competitionFileEntity;
    }
}
