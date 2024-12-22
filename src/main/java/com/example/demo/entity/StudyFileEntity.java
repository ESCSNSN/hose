package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "study_file_table")
public class StudyFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String originalFileName;

    @Column
    private String storedFileName; // 오타 수정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id") // 외래 키 컬럼명 설정
    private StudyEntity studyEntity;

    public static StudyFileEntity toStudyFileEntity(StudyEntity studyEntity, String originalFileName, String storedFileName) {
        StudyFileEntity studyFileEntity = new StudyFileEntity();
        studyFileEntity.setOriginalFileName(originalFileName);
        studyFileEntity.setStoredFileName(storedFileName); // 오타 수정
        studyFileEntity.setStudyEntity(studyEntity);
        return studyFileEntity;
    }
}