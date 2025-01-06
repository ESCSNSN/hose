package com.example.demo.dto;

import com.example.demo.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GraduateDTO {
    private Long id;

    @JsonIgnore
    private String userID;

    private String boardID;
    private String graduateId;
    private String graduateTitle;
    private String graduateContents;
    private String graduateHashtag;


    private LocalDateTime graduateCreatedTime;
    private LocalDateTime graduateUpdatedTime;

    private int scrap;
    private int graduateLike;



    public GraduateDTO(Long id, String graduateId, String graduateTitle,LocalDateTime graduateCreatedTime,Integer graduateLike,Integer scrap) {
        this.id = id;
        this.graduateId = graduateId;
        this.graduateTitle = graduateTitle;
        this.graduateCreatedTime = graduateCreatedTime;
        this.graduateLike = graduateLike;
        this.scrap = scrap;

    }

    public static GraduateDTO toGraduateDTO(GraduateEntity graduateEntity) {
        GraduateDTO graduateDTO = new GraduateDTO();
        graduateDTO.setId(graduateEntity.getId());
        graduateDTO.setUserID(graduateEntity.getUserId());
        graduateDTO.setBoardID(graduateEntity.getBoardId());
        graduateDTO.setGraduateId(graduateEntity.getGraduateId());
        graduateDTO.setGraduateTitle(graduateEntity.getGraduatetitle());
        graduateDTO.setGraduateContents(graduateEntity.getGraduatecontents());
        graduateDTO.setGraduateHashtag(graduateEntity.getGraduatehashtag());
        graduateDTO.setGraduateCreatedTime(graduateEntity.getGraduateCreatedTime());
        graduateDTO.setGraduateUpdatedTime(graduateEntity.getGraduateUpdatedTime());
        graduateDTO.setGraduateLike(graduateEntity.getGraduateLike());
        graduateDTO.setScrap(graduateEntity.getScrap());

        return graduateDTO;
    }
}