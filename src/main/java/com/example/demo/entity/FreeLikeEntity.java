package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "free_like_table",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "free_entity_id"})}
)
public class FreeLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "free_entity_id", nullable = false)
    private FreeEntity freeEntity;

    public static FreeLikeEntity toFreeLikeEntity(FreeEntity freeEntity,String userId) {
        FreeLikeEntity freeLikeEntity = new FreeLikeEntity();
        freeLikeEntity.userId = userId;
        freeLikeEntity.freeEntity = freeEntity;
        return freeLikeEntity;
    }

}
