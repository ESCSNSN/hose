package com.example.demo.dto;

import com.example.demo.entity.QuestEntity;
import com.example.demo.entity.QuestFileEntity;
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
public class StudyDTO {
    private Long id;
    private String userID;
    private String boardID;
    private String studyID;
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

    public StudyDTO(Long id, String studyID, String studyTitle,LocalDateTime deadline,Integer recruit,Integer countMember,Integer scrap) {
        this.id = id;
        this.studyID = studyID;
        this.studyTitle = studyTitle;
        this.deadline = deadline;
        this.recruit = recruit;
        this.countMember = countMember;
        this.scrap = scrap;
    }

    public static StudyDTO toStudyDTO(StudyEntity studyEntity) {
        StudyDTO studyDTO = new StudyDTO();
        studyDTO.setId(studyEntity.getId());
        studyDTO.setUserID(studyEntity.getUserId());
        studyDTO.setBoardID(studyEntity.getBoardId());
        studyDTO.setStudyID(studyEntity.getStudyId());
        studyDTO.setStudyTitle(studyEntity.getStudytitle());
        studyDTO.setStudyContents(studyEntity.getStudtycontents());
        studyDTO.setStudyHashtag(studyEntity.getStudyhashtag());
        studyDTO.setDeadline(studyEntity.getDeadline());
        studyDTO.setRecruit(studyEntity.getRecruit());
        studyDTO.setCountMember(studyEntity.getCountMember());
        studyDTO.setStudyCreatedTime(studyEntity.getStudyCreatedTime());
        studyDTO.setStudyUpdatedTime(studyEntity.getStudyUpdatedTime());
        studyDTO.setScrap(studyEntity.getScrap());
        studyDTO.setStudyLike(studyEntity.getStudyLike());

        if (studyEntity.getFileAttached() == 0) {
            studyDTO.setFileAttached(studyEntity.getFileAttached());
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            studyDTO.setFileAttached(studyEntity.getFileAttached());

            for (StudyFileEntity studyFileEntity : studyEntity.getStudyFileEntityList()) {
                originalFileNameList.add(studyFileEntity.getOriginalFileName());
                storedFileNameList.add(studyFileEntity.getStoredFileName());
            }
            studyDTO.setOriginalFileName(originalFileNameList);
            studyDTO.setStoredFileName(storedFileNameList);
        }
        return studyDTO;
    }
}