package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(
        name = "study_scrap_table",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "study_entity_id"})}
)
public class StudyScrapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "study_id",nullable = false)
    private String studyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_entity_id", nullable = false)
    private StudyEntity studyEntity;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    public static StudyScrapEntity toScrapEntity(StudyEntity studyEntity, String userId) {
        StudyScrapEntity studyScrapEntity = new StudyScrapEntity();
        studyScrapEntity.setUserId(userId);
        studyScrapEntity.setStudyId(studyEntity.getStudyId());
        studyScrapEntity.setStudyEntity(studyEntity);
        return studyScrapEntity;
    }
}
