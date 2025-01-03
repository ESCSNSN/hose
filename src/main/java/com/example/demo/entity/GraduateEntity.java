package com.example.demo.entity;

import com.example.demo.dto.GraduateDTO;
import com.example.demo.dto.QuestDTO;
import com.example.demo.dto.StudyDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "graduate_table")
public class GraduateEntity extends GraduateBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 20, nullable = false)
    private String userId;

    @Column(length = 20, nullable = false, updatable = false)
    private String boardId = "graduate"; // 기본 값을 "study"로 고정하고, 수정 불가로 설정

    @Column(length = 20, nullable = false)
    private String graduateId; //

    @Column(name = "graduate_title")
    private String graduatetitle;

    @Column(name = "graduate_contents", length = 500)
    private String graduatecontents;

    @Column(name = "graduate_like")
    private int graduateLike = 0;  // 좋아요 갯수

    @Column(name = "scrap")
    private int scrap = 0;  // scrap 여부

    @Column(name = "graudate_hashtag")
    private String graduatehashtag;

    @OneToMany(mappedBy = "graduateEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GraduateLikeEntity> likes = new ArrayList<>();

    @OneToMany(mappedBy = "graduateEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GraduateScrapEntity> scraps = new ArrayList<>();



    public static GraduateEntity toSaveEntity(GraduateDTO graduateDTO) {
        GraduateEntity graduateEntity = new GraduateEntity();
       graduateEntity.setUserId(graduateDTO.getUserID());
        graduateEntity.setGraduateId(graduateDTO.getGraduateId());
       graduateEntity.setGraduatetitle(graduateDTO.getGraduateTitle());
       graduateEntity.setGraduatecontents(graduateDTO.getGraduateContents());
       graduateEntity.setGraduatehashtag(graduateDTO.getGraduateHashtag());

        return graduateEntity;
    }

    public static GraduateEntity toUpdatedEntity(GraduateDTO graduateDTO) {

        GraduateEntity graduateEntity = new GraduateEntity();
        graduateEntity.setId(graduateDTO.getId());
        graduateEntity.setUserId(graduateDTO.getUserID());
        graduateEntity.setGraduateId(graduateDTO.getGraduateId());
        graduateEntity.setGraduatetitle(graduateDTO.getGraduateTitle());
        graduateEntity.setGraduatecontents(graduateDTO.getGraduateContents());
        graduateEntity.setGraduatehashtag(graduateDTO.getGraduateHashtag());

        return graduateEntity;
    }


}