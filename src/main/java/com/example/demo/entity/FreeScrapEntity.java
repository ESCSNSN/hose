package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(
        name = "free_scrap_table",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "free_entity_id"})}
)
public class FreeScrapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "free_entity_id", nullable = false)
    private FreeEntity freeEntity;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    public static FreeScrapEntity toScrapEntity(FreeEntity freeEntity, String userId) {
        FreeScrapEntity freeScrapEntity = new FreeScrapEntity();
        freeScrapEntity.setUserId(userId);
        freeScrapEntity.setFreeEntity(freeEntity);
        return freeScrapEntity;
    }
}
