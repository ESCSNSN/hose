package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(
        name = "graduate_scrap_table",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "graduate_entity_id"})}
)
public class GraduateScrapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "graduate_id",nullable = false)
    private String graduateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graduate_entity_id", nullable = false)
    private GraduateEntity graduateEntity;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    public static GraduateScrapEntity toScrapEntity(GraduateEntity graduateEntity, String userId) {
        GraduateScrapEntity graduateScrapEntity = new GraduateScrapEntity();
        graduateScrapEntity.setUserId(userId);
        graduateScrapEntity.setGraduateId(graduateEntity.getGraduateId());
        graduateScrapEntity.setGraduateEntity(graduateEntity);
        return graduateScrapEntity;
    }
}
