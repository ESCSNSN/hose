package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "quest_file_table")
public class QuestFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String originalFileName;

    @Column
    private String storedFileName; // 오타 수정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id") // 외래 키 컬럼명 설정
    private QuestEntity questEntity;

    public static QuestFileEntity toQuestFileEntity(QuestEntity questEntity, String originalFileName, String storedFileName) {
        QuestFileEntity questFileEntity = new QuestFileEntity();
        questFileEntity.setOriginalFileName(originalFileName);
        questFileEntity.setStoredFileName(storedFileName); // 오타 수정
        questFileEntity.setQuestEntity(questEntity);
        return questFileEntity;
    }
}

