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
public class MainCompetitionDTO {

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



    public static MainCompetitionDTO toMainCompetitionDTO(CompetitionEntity competitionEntity) {
        MainCompetitionDTO mainCompetitionDTO = new MainCompetitionDTO();
        mainCompetitionDTO.setId(competitionEntity.getId());
        mainCompetitionDTO.setUserId(competitionEntity.getUserId());
        mainCompetitionDTO.setBoardId(competitionEntity.getBoardId());
        mainCompetitionDTO.setCompetitionTitle(competitionEntity.getCompetitiontitle());
        mainCompetitionDTO.setCompetitionContents(competitionEntity.getCompetitioncontents());
        mainCompetitionDTO.setCompetitionHashtag(competitionEntity.getCompetitionhashtag());
        mainCompetitionDTO.setCompetitionCreatedTime(competitionEntity.getCompetitionCreatedTime());
        mainCompetitionDTO.setCompetitionUpdatedTime(competitionEntity.getCompetitionUpdatedTime());
        mainCompetitionDTO.setScrap(competitionEntity.getScrap());
        mainCompetitionDTO.setCompetitionLike(competitionEntity.getCompetitionLike());

        if (competitionEntity.getFileAttached() == 0) {
            mainCompetitionDTO.setFileAttached(competitionEntity.getFileAttached());
        } else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            mainCompetitionDTO.setFileAttached(competitionEntity.getFileAttached());

            for (CompetitionFileEntity competitionFileEntity : competitionEntity.getCompetitionFileEntityList()) {
                originalFileNameList.add(competitionFileEntity.getOriginalFilename());
                storedFileNameList.add(competitionFileEntity.getStoredFilename());
            }
            mainCompetitionDTO.setOriginalFileName(originalFileNameList);
            mainCompetitionDTO.setStoredFileName(storedFileNameList);
        }
        return mainCompetitionDTO;
    }
}
