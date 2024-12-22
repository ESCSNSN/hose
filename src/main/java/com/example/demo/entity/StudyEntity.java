package com.example.demo.entity;

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
@Table(name = "study_table")
public class StudyEntity extends StudyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 20, nullable = false)
    private String userId;

    @Column(length = 20, nullable = false, updatable = false)
    private String boardId = "study"; // 기본 값을 "study"로 고정하고, 수정 불가로 설정

    @Column(length = 20, nullable = false)
    private String studyId; //

    @Column(name = "study_title")
    private String studytitle;

    @Column(name = "study_contents", length = 500)
    private String studtycontents;

    @Column(name = "deadline")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadline;

    @Column(name = "recruit")
    @Min(value = 1, message = "모집 인원은 1 이상이어야 합니다.")
    private Integer recruit;

    @Column(name = "count_member")
    private int countMember = 0;

    @Column(name = "study_like")
    private int studyLike = 0;  // 좋아요 갯수

    @Column(name = "scrap")
    private int scrap = 0;  // scrap 여부

    @Column(name = "study_hashtag")
    private String studyhashtag;

    @Column
    private int fileAttached; // 1 or 0

    @OneToMany(mappedBy = "studyEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StudyFileEntity> studyFileEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "studyEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ApplyEntity> ApplyEntityList = new ArrayList<>();

    public static StudyEntity toSaveEntity(StudyDTO studyDTO) {
        StudyEntity studyEntity = new StudyEntity();
        studyEntity.setUserId(studyDTO.getUserID());
        studyEntity.setStudyId(studyDTO.getStudyID());
        studyEntity.setStudytitle(studyDTO.getStudyTitle());
        studyEntity.setStudtycontents(studyDTO.getStudyContents());
        studyEntity.setStudyhashtag(studyDTO.getStudyHashtag());
        studyEntity.setDeadline(studyDTO.getDeadline());
        studyEntity.setRecruit(studyDTO.getRecruit());
        studyEntity.setFileAttached(0);
        return studyEntity;
    }

    public static StudyEntity toUpdatedEntity(StudyDTO studyDTO) {
        StudyEntity studyEntity = new StudyEntity();
        studyEntity.setId(studyDTO.getId());
        studyEntity.setUserId(studyDTO.getUserID());
        studyEntity.setStudyId(studyDTO.getStudyID());
        studyEntity.setStudytitle(studyDTO.getStudyTitle());
        studyEntity.setStudtycontents(studyDTO.getStudyContents());
        studyEntity.setStudyhashtag(studyDTO.getStudyHashtag());
        studyEntity.setDeadline(studyDTO.getDeadline());
        studyEntity.setRecruit(studyDTO.getRecruit());
        studyEntity.setFileAttached(studyDTO.getFileAttached());
        return studyEntity;
    }

    public static StudyEntity toSaveFileEntity(StudyDTO studyDTO) {
        StudyEntity studyEntity = new StudyEntity();
        studyEntity.setUserId(studyDTO.getUserID());
        studyEntity.setStudyId(studyDTO.getStudyID());
        studyEntity.setStudytitle(studyDTO.getStudyTitle());
        studyEntity.setStudtycontents(studyDTO.getStudyContents());
        studyEntity.setStudyhashtag(studyDTO.getStudyHashtag());
        studyEntity.setDeadline(studyDTO.getDeadline());
        studyEntity.setRecruit(studyDTO.getRecruit());
        studyEntity.setFileAttached(1);
        return studyEntity;
    }
}