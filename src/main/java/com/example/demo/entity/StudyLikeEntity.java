package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "study_like_table",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "quest_entity_id"})}
)
public class StudyLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_entity_id", nullable = false)
    private StudyEntity studyEntity;

    public static StudyLikeEntity toStudyLikeEntity(StudyEntity studyEntity,String userId) {
        StudyLikeEntity studyLikeEntity = new StudyLikeEntity();
        studyLikeEntity.userId = userId;
        studyLikeEntity.studyEntity = studyEntity;
        return studyLikeEntity;
    }

}
