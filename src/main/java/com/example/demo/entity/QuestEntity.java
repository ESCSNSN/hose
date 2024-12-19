package com.example.demo.entity;

import com.example.demo.dto.QuestDTO;
import com.example.demo.repository.QuestFileRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "quest_table")
public class QuestEntity extends QuestBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 20, nullable = false)
    private String userId;

    @Column(length = 20, nullable = false, updatable = false)
    private String boardId = "quest"; // 기본 값을 "quset"로 고정하고, 수정 불가로 설정

    @Column(name = "quest_title")
    private String questtitle;

    @Column(name = "quest_contents", length = 500)
    private String questcontents;

    @Column(name = "quest_like")
    private int questLike = 0;  // 좋아요 갯수

    @Column(name = "scrap")
    private int scrap = 0;  // scrap 여부

    @Column(name = "quest_hashtag")
    private String questhashtag;

    @Column
    private int fileAttached; // 1 or 0

    @OneToMany(mappedBy = "questEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuestFileEntity> questFileEntityList = new ArrayList<>();

    public static QuestEntity toSaveEntity(QuestDTO questDTO) {
        QuestEntity questEntity = new QuestEntity();
        questEntity.setUserId(questDTO.getUserID());
        questEntity.setQuesttitle(questDTO.getQuestTitle());
        questEntity.setQuestcontents(questDTO.getQuestContents());
        questEntity.setQuesthashtag(questDTO.getQuestHashtag());
        questEntity.setFileAttached(0);
        return questEntity;
    }

    public static QuestEntity toUpdatedEntity(QuestDTO questDTO) {
        QuestEntity questEntity = new QuestEntity();
        questEntity.setId(questDTO.getId());
        questEntity.setUserId(questDTO.getUserID());
        questEntity.setQuesttitle(questDTO.getQuestTitle());
        questEntity.setQuestcontents(questDTO.getQuestContents());
        questEntity.setQuesthashtag(questDTO.getQuestHashtag());
        return questEntity;
    }

    public static QuestEntity toSaveFileEntity(QuestDTO questDTO) {
        QuestEntity questEntity = new QuestEntity();
        questEntity.setUserId(questDTO.getUserID());
        questEntity.setQuesttitle(questDTO.getQuestTitle());
        questEntity.setQuestcontents(questDTO.getQuestContents());
        questEntity.setQuesthashtag(questDTO.getQuestHashtag());
        questEntity.setFileAttached(1);
        return questEntity;
    }
}
