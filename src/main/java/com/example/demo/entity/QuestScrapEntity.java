package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(
        name = "quest_scrap_table",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "quest_entity_id"})}
)
public class QuestScrapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_entity_id", nullable = false)
    private QuestEntity questEntity;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    public static QuestScrapEntity toScrapEntity(QuestEntity questEntity, String userId) {
        QuestScrapEntity scrapEntity = new QuestScrapEntity();
        scrapEntity.setUserId(userId);
        scrapEntity.setQuestEntity(questEntity);
        return scrapEntity;
    }
}
