package com.example.demo.dto;


import com.example.demo.entity.QuestEntity;
import com.example.demo.entity.QuestFileEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class QuestDTO {
    private Long id;
    @JsonIgnore
    private String userID;
    private String boardID;
    private String questTitle;
    private String questContents;
    private String questHashtag;

    private LocalDateTime questCreatedTime;
    private LocalDateTime questUpdatedTime;

    private int scrap;
    private int questLike;
    private boolean scrapped; // 사용자가 스크랩했는지 여부 추가

    @JsonIgnore
    private List<MultipartFile> questFile;
    private List<String> originalFileName;
    private List<String> storedFileName;
    private int fileAttached;
    private List<String> imageUrls;

    public QuestDTO(Long id, String questTitle, LocalDateTime questCreatedTime,Integer questLike,Integer scrap,boolean scrapped) {
        this.id = id;
        this.questTitle = questTitle;
        this.questCreatedTime = questCreatedTime;
        this.questLike = questLike;
        this.scrap = scrap;
        this.scrapped = scrapped;
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

            // 이미지 URL 생성
            // 이미지 URL 생성
            List<String> imageUrls = questEntity.getQuestFileEntityList().stream()
                    .map(file -> {
                        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                        return baseUrl + "/upload/" + file.getStoredFileName();
                    })
                    .collect(Collectors.toList());
            questDTO.setImageUrls(imageUrls);

        }
        return questDTO;
    }
}
