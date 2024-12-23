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
public class MainCodingDTO {
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



    public static MainCodingDTO toMainCodingDTO(CodingEntity codingEntity) {
        MainCodingDTO mainCodingDTO = new MainCodingDTO();
        mainCodingDTO.setId(codingEntity.getId());
        mainCodingDTO.setUserID(codingEntity.getUserId());
        mainCodingDTO.setBoardID(codingEntity.getBoardId());
        mainCodingDTO.setCodingTitle(codingEntity.getCodingtitle());
        mainCodingDTO.setCodingContents(codingEntity.getCodingcontents());
        mainCodingDTO.setCodingHashtag(codingEntity.getCodinghashtag());
        mainCodingDTO.setCodingType(codingEntity.getCodingtype());
        mainCodingDTO.setCodingCreatedTime(codingEntity.getCodingCreatedTime());
        mainCodingDTO.setCodingUpdatedTime(codingEntity.getCodingUpdatedTime());
        mainCodingDTO.setScrap(codingEntity.getScrap());
        mainCodingDTO.setCodingLike(codingEntity.getCodingLike());

        if (codingEntity.getFileAttached() == 0) {
            mainCodingDTO.setFileAttached(codingEntity.getFileAttached());
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            mainCodingDTO.setFileAttached(codingEntity.getFileAttached());

            for (CodingFileEntity codingFileEntity : codingEntity.getCodingFileEntityList()) {
                originalFileNameList.add(codingFileEntity.getOriginalFileName());
                storedFileNameList.add(codingFileEntity.getStoredFileName());
            }
            mainCodingDTO.setOriginalFileName(originalFileNameList);
            mainCodingDTO.setStoredFileName(storedFileNameList);
        }
        return mainCodingDTO;
    }

}
