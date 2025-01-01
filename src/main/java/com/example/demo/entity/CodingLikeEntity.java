package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "coding_like_table",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "coding_entity_id"})}
)
public class CodingLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coding_entity_id", nullable = false)
    private CodingEntity codingEntity;

    public static CodingLikeEntity toCodingLikeEntity(CodingEntity codingEntity,String userId) {
        CodingLikeEntity codingLikeEntity = new CodingLikeEntity();
        codingLikeEntity.userId = userId;
        codingLikeEntity.codingEntity = codingEntity;
        return codingLikeEntity;
    }

}
