package com.example.demo.dto;

import com.example.demo.entity.CodingEntity;
import com.example.demo.entity.CodingFileEntity;
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
public class CodingDTO {
    private Long id;
    private String userID;
    private String boardID;
    private String codingTitle;
    private String codingContents;
    private String codingHashtag;
    private String codingType;

    private LocalDateTime codingCreatedTime;
    private LocalDateTime codingUpdatedTime;

    private int scrap;
    private int codingLike;

    @JsonIgnore
    private List<MultipartFile> codingFile;
    private List<String> originalFileName;
    private List<String> storedFileName;
    private int fileAttached;

    public CodingDTO(Long id, String codingType, String codingTitle, LocalDateTime codingCreatedTime,Integer scrap) {
        this.id = id;
        this.codingType = codingType;
        this.codingTitle = codingTitle;
        this.codingCreatedTime = codingCreatedTime;
        this.scrap = scrap;
    }

    public static CodingDTO toCodingDTO(CodingEntity codingEntity) {
        CodingDTO codingDTO = new CodingDTO();
        codingDTO.setId(codingEntity.getId());
        codingDTO.setUserID(codingEntity.getUserId());
        codingDTO.setBoardID(codingEntity.getBoardId());
        codingDTO.setCodingTitle(codingEntity.getCodingtitle());
        codingDTO.setCodingContents(codingEntity.getCodingcontents());
        codingDTO.setCodingHashtag(codingEntity.getCodinghashtag());
        codingDTO.setCodingType(codingEntity.getCodingtype());
        codingDTO.setCodingCreatedTime(codingEntity.getCodingCreatedTime());
        codingDTO.setCodingUpdatedTime(codingEntity.getCodingUpdatedTime());
        codingDTO.setScrap(codingEntity.getScrap());
        codingDTO.setCodingLike(codingEntity.getCodingLike());

        if (codingEntity.getFileAttached() == 0) {
            codingDTO.setFileAttached(codingEntity.getFileAttached());
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            codingDTO.setFileAttached(codingEntity.getFileAttached());

            for (CodingFileEntity codingFileEntity : codingEntity.getCodingFileEntityList()) {
                originalFileNameList.add(codingFileEntity.getOriginalFileName());
                storedFileNameList.add(codingFileEntity.getStoredFileName());
            }
            codingDTO.setOriginalFileName(originalFileNameList);
            codingDTO.setStoredFileName(storedFileNameList);
        }
        return codingDTO;
    }

}
