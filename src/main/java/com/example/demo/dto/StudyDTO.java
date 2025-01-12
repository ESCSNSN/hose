package com.example.demo.dto;

import com.example.demo.entity.QuestEntity;
import com.example.demo.entity.QuestFileEntity;
import com.example.demo.entity.StudyEntity;
import com.example.demo.entity.StudyFileEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StudyDTO {
    private Long id;
    @JsonIgnore
    private String userID;
    private String boardID;
    private String studyID;
    private String studyTitle;
    private String studyContents;
    private String studyHashtag;

    private LocalDateTime startTime;
    private LocalDateTime deadline;
    private Integer recruit;
    private int countMember;

    private LocalDateTime studyCreatedTime;
    private LocalDateTime studyUpdatedTime;

    private int scrap;
    private int studyLike;

    private boolean scrapped; // 사용자가 스크랩했는지 여부 추가
    private long daysLeft;

    @JsonIgnore
    private List<MultipartFile> studyFile;
    private List<String> originalFileName;
    private List<String> storedFileName;
    private int fileAttached;
    private List<String> imageUrls;

    public StudyDTO(Long id, String studyID, String studyTitle,LocalDateTime startTime,LocalDateTime deadline,Integer recruit,Integer countMember,Integer scrap,Long daysLeft,boolean scrapped) {
        this.id = id;
        this.studyID = studyID;
        this.studyTitle = studyTitle;
        this.startTime = startTime;
        this.deadline = deadline;
        this.recruit = recruit;
        this.countMember = countMember;
        this.scrap = scrap;
        this.daysLeft = daysLeft;
        this.scrapped = scrapped;
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
        studyDTO.setStartTime(studyEntity.getStartTime());
        studyDTO.setDeadline(studyEntity.getDeadline());
        studyDTO.setRecruit(studyEntity.getRecruit());
        studyDTO.setCountMember(studyEntity.getCountMember());
        studyDTO.setStudyCreatedTime(studyEntity.getStudyCreatedTime());
        studyDTO.setStudyUpdatedTime(studyEntity.getStudyUpdatedTime());
        studyDTO.setScrap(studyEntity.getScrap());
        studyDTO.setStudyLike(studyEntity.getStudyLike());

        // daysLeft 계산 및 설정
        if (studyEntity.getDeadline() != null) {
            LocalDate today = LocalDate.now();
            LocalDate deadlineDate = studyEntity.getDeadline().toLocalDate();
            long daysLeft = ChronoUnit.DAYS.between(today, deadlineDate);
            studyDTO.setDaysLeft(daysLeft);
        }

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
            List<String> imageUrls = studyEntity.getStudyFileEntityList().stream()
                    .map(file -> "https://kr.object.ncloudstorage.com" + "/" + "info0704" + "/" + file.getStoredFileName())
                    .collect(Collectors.toList());
            studyDTO.setImageUrls(imageUrls);
        }
        return studyDTO;
    }
}