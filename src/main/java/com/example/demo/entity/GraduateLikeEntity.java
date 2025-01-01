package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "graduate_like_table",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "graduate_entity_id"})}
)
public class GraduateLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graduate_entity_id", nullable = false)
    private GraduateEntity graduateEntity;

    public static GraduateLikeEntity toGraduateLikeEntity(GraduateEntity graduateEntity,String userId) {
        GraduateLikeEntity graduateLikeEntity = new GraduateLikeEntity();
        graduateLikeEntity.userId = userId;
        graduateLikeEntity.graduateEntity = graduateEntity;
        return graduateLikeEntity;
    }

}
