package com.example.demo.dto;

import com.example.demo.entity.CompetitionEntity;
import com.example.demo.entity.CompetitionFileEntity;
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
public class CompetitionDTO {

    private Long id;
    private String userId;
    private String boardId;
    private String competitionTitle;
    private String competitionContents;
    private String competitionHashtag;

    private LocalDateTime competitionCreatedTime;
    private LocalDateTime competitionUpdatedTime;

    private int scrap; // scrap 여부
    private int competitionLike; // 좋아요 여부

    // 파일 첨부를 위한 부분
    @JsonIgnore
    private List<MultipartFile> competitionFile;
    private List<String> originalFileName; // 오타 수정
    private List<String> storedFileName;
    private int fileAttached;

    public CompetitionDTO(Long id, String userId, String competitionTitle, LocalDateTime competitionCreatedTime, Integer scrap) {
        this.id = id;
        this.competitionTitle = competitionTitle;
        this.competitionCreatedTime = competitionCreatedTime;
        this.scrap = scrap;
    }

    public static CompetitionDTO toCompetitionDTO(CompetitionEntity competitionEntity) {
        CompetitionDTO competitionDTO = new CompetitionDTO();
        competitionDTO.setId(competitionEntity.getId());
        competitionDTO.setUserId(competitionEntity.getUserId());
        competitionDTO.setBoardId(competitionEntity.getBoardId());
        competitionDTO.setCompetitionTitle(competitionEntity.getCompetitiontitle());
        competitionDTO.setCompetitionContents(competitionEntity.getCompetitioncontents());
        competitionDTO.setCompetitionHashtag(competitionEntity.getCompetitionhashtag());
       competitionDTO.setCompetitionCreatedTime(competitionEntity.getCompetitionCreatedTime());
       competitionDTO.setCompetitionUpdatedTime(competitionEntity.getCompetitionUpdatedTime());
        competitionDTO.setScrap(competitionEntity.getScrap());
        competitionDTO.setCompetitionLike(competitionEntity.getCompetitionLike());

        if (competitionEntity.getFileAttached() == 0) {
            competitionDTO.setFileAttached(competitionEntity.getFileAttached());
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            competitionDTO.setFileAttached(competitionEntity.getFileAttached());

            for (CompetitionFileEntity competitionFileEntity : competitionEntity.getCompetitionFileEntityList()) {
                originalFileNameList.add(competitionFileEntity.getOriginalFilename());
                storedFileNameList.add(competitionFileEntity.getStoredFilename());
            }
            competitionDTO.setOriginalFileName(originalFileNameList);
            competitionDTO.setStoredFileName(storedFileNameList);
        }
        return competitionDTO;
    }
}
