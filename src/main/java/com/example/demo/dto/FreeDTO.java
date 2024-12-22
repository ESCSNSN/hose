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
public class FreeDTO {
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

    public FreeDTO(Long id, String freeTitle, LocalDateTime freeCreatedTime,Integer freeLike,Integer scrap) {
        this.id = id;
        this.freeTitle = freeTitle;
        this.freeCreatedTime = freeCreatedTime;
        this.freeLike = freeLike;
        this.scrap = scrap;
    }

    public static FreeDTO toFreeDTO(FreeEntity freeEntity) {
        FreeDTO freeDTO = new FreeDTO();
        freeDTO.setId(freeEntity.getId());
        freeDTO.setUserID(freeEntity.getUserId());
        freeDTO.setBoardID(freeEntity.getBoardId());
        freeDTO.setFreeTitle(freeEntity.getFreetitle());
        freeDTO.setFreeContents(freeEntity.getFreecontents());
        freeDTO.setFreeHashtag(freeEntity.getFreehashtag());
        freeDTO.setFreeCreatedTime(freeEntity.getFreeCreatedTime());
        freeDTO.setFreeUpdatedTime(freeEntity.getFreeUpdatedTime());
        freeDTO.setScrap(freeEntity.getScrap());
        freeDTO.setFreeLike(freeEntity.getFreeLike());

        if (freeEntity.getFileAttached() == 0) {
            freeDTO.setFileAttached(freeEntity.getFileAttached());
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            freeDTO.setFileAttached(freeEntity.getFileAttached());

            for (FreeFileEntity freeFileEntity : freeEntity.getFreeFileEntityList()) {
                originalFileNameList.add(freeFileEntity.getOriginalFileName());
                storedFileNameList.add(freeFileEntity.getStoredFileName());
            }
            freeDTO.setOriginalFileName(originalFileNameList);
            freeDTO.setStoredFileName(storedFileNameList);
        }
        return freeDTO;
    }
}
