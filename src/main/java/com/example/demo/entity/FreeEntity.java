package com.example.demo.entity;

import com.example.demo.dto.CodingDTO;
import com.example.demo.dto.FreeDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "free_table")
public class FreeEntity extends FreeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 20, nullable = false)
    private String userId;

    @Column(length = 20, nullable = false, updatable = false)
    private String boardId = "free"; // 기본 값을 "code"로 고정하고, 수정 불가로 설정

    @Column(name = "free_title")
    private String freetitle;

    @Column(name = "free_contents", length = 500)
    private String freecontents;

    @Column(name = "free_like")
    private int freeLike = 0;  // 좋아요 갯수

    @Column(name = "scrap")
    private int scrap = 0;  // scrap 여부

    @Column(name = "free_hashtag")
    private String freehashtag;

    @Column
    private int fileAttached; // 1 or 0

    @OneToMany(mappedBy = "freeEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FreeFileEntity> freeFileEntityList = new ArrayList<>();

    public static FreeEntity toSaveEntity(FreeDTO freeDTO) {
        FreeEntity freeEntity = new FreeEntity();
        freeEntity.setUserId(freeDTO.getUserID());
        freeEntity.setFreetitle(freeDTO.getFreeTitle());
        freeEntity.setFreecontents(freeDTO.getFreeContents());
        freeEntity.setFreehashtag(freeDTO.getFreeHashtag());
        freeEntity.setFileAttached(0);
        return freeEntity;
    }

    public static FreeEntity toUpdatedEntity(FreeDTO freeDTO) {
        FreeEntity freeEntity = new FreeEntity();
        freeEntity.setId(freeDTO.getId());
        freeEntity.setUserId(freeDTO.getUserID());
        freeEntity.setFreetitle(freeDTO.getFreeTitle());
        freeEntity.setFreecontents(freeDTO.getFreeContents());
        freeEntity.setFreehashtag(freeDTO.getFreeHashtag());
        return freeEntity;
    }

    public static FreeEntity toSaveFileEntity(FreeDTO freeDTO) {
        FreeEntity freeEntity = new FreeEntity();
        freeEntity.setUserId(freeDTO.getUserID());
        freeEntity.setFreetitle(freeDTO.getFreeTitle());
        freeEntity.setFreecontents(freeDTO.getFreeContents());
        freeEntity.setFreehashtag(freeDTO.getFreeHashtag());
        freeEntity.setFileAttached(1);
        return freeEntity;
    }
}
