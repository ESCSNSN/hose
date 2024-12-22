package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "apply_table", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"apply_userid", "study_id"})
})
public class ApplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "apply_userid", nullable = false)
    private String applyUserId;

    @Column(name = "accept", nullable = false)
    private boolean accept = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private StudyEntity studyEntity;

    public boolean isAccept() {
        return this.accept;
    }

    public static ApplyEntity toApplyEntity(StudyEntity studyEntity, String applyUserId, boolean accept) {
        ApplyEntity applyEntity = new ApplyEntity();
        applyEntity.setApplyUserId(applyUserId);
        applyEntity.setAccept(accept);
        applyEntity.setStudyEntity(studyEntity);
        return applyEntity;
    }
}
