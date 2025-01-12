package com.example.demo.dto;

import com.example.demo.entity.*;
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
    private boolean scrapped; // 사용자가 스크랩했는지 여부 추가

    @JsonIgnore
    private List<MultipartFile> graduateFile;
    private List<String> originalFileName;
    private List<String> storedFileName;
    private int fileAttached;
    private List<String> imageUrls;




    public GraduateDTO(Long id, String graduateId, String graduateTitle,LocalDateTime graduateCreatedTime,Integer graduateLike,Integer scrap,boolean scrapped) {
        this.id = id;
        this.graduateId = graduateId;
        this.graduateTitle = graduateTitle;
        this.graduateCreatedTime = graduateCreatedTime;
        this.graduateLike = graduateLike;
        this.scrap = scrap;
        this.scrapped = scrapped;

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

        if (graduateEntity.getFileAttached() == 0) {
            graduateDTO.setFileAttached(graduateEntity.getFileAttached());
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            graduateDTO.setFileAttached(graduateEntity.getFileAttached());

            for (GraduateFileEntity graduateFileEntity : graduateEntity.getGraduateFileEntityList()) {
                originalFileNameList.add(graduateFileEntity.getOriginalFilename());
                storedFileNameList.add(graduateFileEntity.getStoredFilename());
            }
            graduateDTO.setOriginalFileName(originalFileNameList);
            graduateDTO.setStoredFileName(storedFileNameList);

            List<String> imageUrls = graduateEntity.getGraduateFileEntityList().stream()
                    .map(file -> "https://kr.object.ncloudstorage.com" + "/" + "info0704" + "/" + file.getStoredFilename())
                    .collect(Collectors.toList());
            graduateDTO.setImageUrls(imageUrls);
        }
        return graduateDTO;

    }
}