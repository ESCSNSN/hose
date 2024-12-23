package com.example.demo.dto;

import com.example.demo.entity.StudyEntity;
import com.example.demo.entity.StudyFileEntity;
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
public class MainStudyDTO {
    private Long id;
    private String userID;
    private String boardID;
    private String studyID; // 카테고리: "bootcamp", "industry", "study"
    private String studyTitle;
    private String studyContents;
    private String studyHashtag;

    private LocalDateTime deadline;
    private Integer recruit;
    private int countMember;

    private LocalDateTime studyCreatedTime;
    private LocalDateTime studyUpdatedTime;

    private int scrap;
    private int studyLike;

    @JsonIgnore
    private List<MultipartFile> studyFile;
    private List<String> originalFileName;
    private List<String> storedFileName;
    private int fileAttached;

    public static MainStudyDTO toMainStudyDTO(StudyEntity studyEntity) {
        MainStudyDTO mainStudyDTO = new MainStudyDTO();
        mainStudyDTO.setId(studyEntity.getId());
        mainStudyDTO.setUserID(studyEntity.getUserId());
        mainStudyDTO.setBoardID(studyEntity.getBoardId());
        mainStudyDTO.setStudyID(studyEntity.getStudyId()); // 카테고리 설정
        mainStudyDTO.setStudyTitle(studyEntity.getStudytitle());
        mainStudyDTO.setStudyContents(studyEntity.getStudtycontents());
        mainStudyDTO.setStudyHashtag(studyEntity.getStudyhashtag());
        mainStudyDTO.setDeadline(studyEntity.getDeadline());
        mainStudyDTO.setRecruit(studyEntity.getRecruit());
        mainStudyDTO.setCountMember(studyEntity.getCountMember());
        mainStudyDTO.setStudyCreatedTime(studyEntity.getStudyCreatedTime());
        mainStudyDTO.setStudyUpdatedTime(studyEntity.getStudyUpdatedTime());
        mainStudyDTO.setScrap(studyEntity.getScrap());
        mainStudyDTO.setStudyLike(studyEntity.getStudyLike());
        mainStudyDTO.setStudyID(studyEntity.getStudyId()); // 카테고리 설정

        if (studyEntity.getFileAttached() == 0) {
            mainStudyDTO.setFileAttached(studyEntity.getFileAttached());
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            mainStudyDTO.setFileAttached(studyEntity.getFileAttached());

            for (StudyFileEntity studyFileEntity : studyEntity.getStudyFileEntityList()) {
                originalFileNameList.add(studyFileEntity.getOriginalFileName());
                storedFileNameList.add(studyFileEntity.getStoredFileName());
            }
            mainStudyDTO.setOriginalFileName(originalFileNameList);
            mainStudyDTO.setStoredFileName(storedFileNameList);
        }
        return mainStudyDTO;
    }
}
