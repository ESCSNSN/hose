package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "quest_like_table",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "quest_entity_id"})}
)
public class QuestLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_entity_id", nullable = false)
    private QuestEntity questEntity;

    public static QuestLikeEntity toQuestLikeEntity(QuestEntity questEntity,String userId) {
        QuestLikeEntity questLikeEntity = new QuestLikeEntity();
        questLikeEntity.userId = userId;
        questLikeEntity.questEntity = questEntity;
        return questLikeEntity;
    }

}
