package com.example.demo.entity;
import com.example.demo.dto.CompetitionDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "competition_table")
public class CompetitionEntity extends CompetitionBaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 20, nullable = false)
    private String userId;

    @Column(length = 20, nullable = false)
    private String boardId;

    @Column(name = "competition_title")
    private String competitiontitle;
    // camelCase로 수정
    @Column(name = "competition_contents",length = 500)
    private String competitioncontents;  // camelCase로 수정

    @Column(name = "competition_like")
    private int competitionLike = 0;  // camelCase로 수정

    @Column(name = "scrap")
    private int scrap = 0;  // camelCase로 수정

    @Column(name = "competition_hashtag")
    private String competitionhashtag;  // camelCase로 수정

    @Column
    private int fileAttached; // 1 or 0

    @OneToMany(mappedBy = "competitionEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CompetitionFileEntity> competitionFileEntityList = new ArrayList<>();

    public static CompetitionEntity toSaveEntity(CompetitionDTO competitionDTO) {
        CompetitionEntity competitionEntity = new CompetitionEntity();
        competitionEntity.setUserId(competitionDTO.getUserId());
        competitionEntity.setBoardId(competitionDTO.getBoardId());
        competitionEntity.setCompetitiontitle(competitionDTO.getCompetitionTitle());
        competitionEntity.setCompetitioncontents(competitionDTO.getCompetitionContents());
        competitionEntity.setCompetitionhashtag(competitionDTO.getCompetitionHashtag());
        competitionEntity.setCompetitionLike(competitionDTO.getCompetitionLike());
        competitionEntity.setScrap(competitionDTO.getScrap());
        competitionEntity.setFileAttached(0);
        return competitionEntity;
    }

    public static CompetitionEntity toUpdatedEntity(CompetitionDTO competitionDTO) {
        CompetitionEntity competitionEntity = new CompetitionEntity();
        competitionEntity.setId(competitionDTO.getId());
        competitionEntity.setUserId(competitionDTO.getUserId());
        competitionEntity.setBoardId(competitionDTO.getBoardId());
        competitionEntity.setCompetitiontitle(competitionDTO.getCompetitionTitle());
        competitionEntity.setCompetitioncontents(competitionDTO.getCompetitionContents());
        competitionEntity.setCompetitionhashtag(competitionDTO.getCompetitionHashtag());
        return competitionEntity;
    }

    public static CompetitionEntity toSaveFileEntity(CompetitionDTO competitionDTO) {
        CompetitionEntity competitionEntity = new CompetitionEntity();
        competitionEntity.setUserId(competitionDTO.getUserId());
        competitionEntity.setBoardId(competitionDTO.getBoardId());
        competitionEntity.setCompetitiontitle(competitionDTO.getCompetitionTitle());
        competitionEntity.setCompetitioncontents(competitionDTO.getCompetitionContents());
        competitionEntity.setCompetitionhashtag(competitionDTO.getCompetitionHashtag());
        competitionEntity.setCompetitionLike(competitionDTO.getCompetitionLike());
        competitionEntity.setScrap(competitionDTO.getScrap());
        competitionEntity.setFileAttached(1);
        return competitionEntity;
    }


}






