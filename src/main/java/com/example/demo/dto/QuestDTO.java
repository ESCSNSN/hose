package com.example.demo.dto;


import com.example.demo.entity.QuestEntity;
import com.example.demo.entity.QuestFileEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class QuestDTO {
    private Long id;
    private String userID;
    private String boardID;
    private String questTitle;
    private String questContents;
    private String questHashtag;

    private LocalDateTime questCreatedTime;
    private LocalDateTime questUpdatedTime;

    private int scrap;
    private int questLike;

    private List<MultipartFile> questFile;
    private List<String> originalFileName;
    private List<String> storedFileName;
    private int fileAttached;

    public QuestDTO(Long id, String questTitle, LocalDateTime questCreatedTime,Integer questLike,Integer scrap) {
        this.id = id;
        this.questTitle = questTitle;
        this.questCreatedTime = questCreatedTime;
        this.questLike = questLike;
        this.scrap = scrap;
    }

    public static QuestDTO toQuestDTO(QuestEntity questEntity) {
        QuestDTO questDTO = new QuestDTO();
        questDTO.setId(questEntity.getId());
        questDTO.setUserID(questEntity.getUserId());
        questDTO.setBoardID(questEntity.getBoardId());
        questDTO.setQuestTitle(questEntity.getQuesttitle());
        questDTO.setQuestContents(questEntity.getQuestcontents());
        questDTO.setQuestHashtag(questEntity.getQuesthashtag());
        questDTO.setQuestCreatedTime(questEntity.getQuestCreatedTime());
        questDTO.setQuestUpdatedTime(questEntity.getQuestUpdatedTime());
        questDTO.setScrap(questEntity.getScrap());
        questDTO.setQuestLike(questEntity.getQuestLike());

        if (questEntity.getFileAttached() == 0) {
            questDTO.setFileAttached(questEntity.getFileAttached());
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            questDTO.setFileAttached(questEntity.getFileAttached());

            for (QuestFileEntity questFileEntity : questEntity.getQuestFileEntityList()) {
                originalFileNameList.add(questFileEntity.getOriginalFileName());
                storedFileNameList.add(questFileEntity.getStoredFileName());
            }
            questDTO.setOriginalFileName(originalFileNameList);
            questDTO.setStoredFileName(storedFileNameList);
        }
        return questDTO;
    }
}
