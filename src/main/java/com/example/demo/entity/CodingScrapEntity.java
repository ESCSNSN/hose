package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(
        name = "coding_scrap_table",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "coding_entity_id"})}
)
public class CodingScrapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coding_entity_id", nullable = false)
    private CodingEntity codingEntity;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    public static CodingScrapEntity toScrapEntity(CodingEntity codingEntity, String userId) {
        CodingScrapEntity codingScrapEntity = new CodingScrapEntity();
        codingScrapEntity.setUserId(userId);
        codingScrapEntity.setCodingEntity(codingEntity);
        return codingScrapEntity;
    }
}
