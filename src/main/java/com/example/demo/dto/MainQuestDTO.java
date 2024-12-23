package com.example.demo.dto;


import com.example.demo.entity.QuestEntity;
import com.example.demo.entity.QuestFileEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class MainQuestDTO {
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

    @JsonIgnore
    private List<MultipartFile> questFile;
    private List<String> originalFileName;
    private List<String> storedFileName;
    private int fileAttached;


    public static MainQuestDTO toMainQuestDTO(QuestEntity questEntity) {
        MainQuestDTO mainQuestDTO = new MainQuestDTO();
        mainQuestDTO.setId(questEntity.getId());
        mainQuestDTO.setUserID(questEntity.getUserId());
        mainQuestDTO.setBoardID(questEntity.getBoardId());
        mainQuestDTO.setQuestTitle(questEntity.getQuesttitle());
        mainQuestDTO.setQuestContents(questEntity.getQuestcontents());
        mainQuestDTO.setQuestHashtag(questEntity.getQuesthashtag());
        mainQuestDTO.setQuestCreatedTime(questEntity.getQuestCreatedTime());
        mainQuestDTO.setQuestUpdatedTime(questEntity.getQuestUpdatedTime());
        mainQuestDTO.setScrap(questEntity.getScrap());
        mainQuestDTO.setQuestLike(questEntity.getQuestLike());

        if (questEntity.getFileAttached() == 0) {
            mainQuestDTO.setFileAttached(questEntity.getFileAttached());
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            mainQuestDTO.setFileAttached(questEntity.getFileAttached());

            for (QuestFileEntity questFileEntity : questEntity.getQuestFileEntityList()) {
                originalFileNameList.add(questFileEntity.getOriginalFileName());
                storedFileNameList.add(questFileEntity.getStoredFileName());
            }
            mainQuestDTO.setOriginalFileName(originalFileNameList);
            mainQuestDTO.setStoredFileName(storedFileNameList);
        }
        return mainQuestDTO;
    }
}
