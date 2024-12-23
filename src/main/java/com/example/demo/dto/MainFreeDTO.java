package com.example.demo.dto;

import com.example.demo.entity.CodingEntity;
import com.example.demo.entity.CodingFileEntity;
import com.example.demo.entity.FreeEntity;
import com.example.demo.entity.FreeFileEntity;
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
public class MainFreeDTO {
    private Long id;
    private String userID;
    private String boardID;
    private String freeTitle;
    private String freeContents;
    private String freeHashtag;

    private LocalDateTime freeCreatedTime;
    private LocalDateTime freeUpdatedTime;

    private int scrap;
    private int freeLike;

    @JsonIgnore
    private List<MultipartFile> freeFile;
    private List<String> originalFileName;
    private List<String> storedFileName;
    private int fileAttached;


    public static MainFreeDTO toMainFreeDTO(FreeEntity freeEntity) {
        MainFreeDTO mainFreeDTO = new MainFreeDTO();
        mainFreeDTO.setId(freeEntity.getId());
        mainFreeDTO.setUserID(freeEntity.getUserId());
        mainFreeDTO.setBoardID(freeEntity.getBoardId());
        mainFreeDTO.setFreeTitle(freeEntity.getFreetitle());
        mainFreeDTO.setFreeContents(freeEntity.getFreecontents());
        mainFreeDTO.setFreeHashtag(freeEntity.getFreehashtag());
        mainFreeDTO.setFreeCreatedTime(freeEntity.getFreeCreatedTime());
        mainFreeDTO.setFreeUpdatedTime(freeEntity.getFreeUpdatedTime());
        mainFreeDTO.setScrap(freeEntity.getScrap());
        mainFreeDTO.setFreeLike(freeEntity.getFreeLike());

        if (freeEntity.getFileAttached() == 0) {
            mainFreeDTO.setFileAttached(freeEntity.getFileAttached());
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            mainFreeDTO.setFileAttached(freeEntity.getFileAttached());

            for (FreeFileEntity freeFileEntity : freeEntity.getFreeFileEntityList()) {
                originalFileNameList.add(freeFileEntity.getOriginalFileName());
                storedFileNameList.add(freeFileEntity.getStoredFileName());
            }
            mainFreeDTO.setOriginalFileName(originalFileNameList);
            mainFreeDTO.setStoredFileName(storedFileNameList);
        }
        return mainFreeDTO;
    }
}
