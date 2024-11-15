package com.example.demo.entity;

import com.example.demo.dto.CodingDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "coding_table")
public class CodingEntity extends CodingBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 20, nullable = false)
    private String userId;

    @Column(length = 20, nullable = false, updatable = false)
    private String boardId = "code"; // 기본 값을 "code"로 고정하고, 수정 불가로 설정

    @Column(name = "coding_title")
    private String codingtitle;

    @Column(name = "coding_contents", length = 500)
    private String codingcontents;

    @Column(name = "coding_like")
    private int codingLike = 0;  // 좋아요 갯수

    @Column(name = "scrap")
    private int scrap = 0;  // scrap 여부

    @Column(name = "coding_hashtag")
    private String codinghashtag;

    @Column(name = "coding_type")
    private String codingtype;

    @Column
    private int fileAttached; // 1 or 0

    @OneToMany(mappedBy = "codingEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CodingFileEntity> codingFileEntityList = new ArrayList<>();

    public static CodingEntity toSaveEntity(CodingDTO codingDTO) {
        CodingEntity codingEntity = new CodingEntity();
        codingEntity.setUserId(codingDTO.getUserID());
        codingEntity.setCodingtitle(codingDTO.getCodingTitle());
        codingEntity.setCodingcontents(codingDTO.getCodingContents());
        codingEntity.setCodinghashtag(codingDTO.getCodingHashtag());
        codingEntity.setCodingtype(codingDTO.getCodingType());
        codingEntity.setFileAttached(0);
        return codingEntity;
    }

    public static CodingEntity toUpdatedEntity(CodingDTO codingDTO) {
        CodingEntity codingEntity = new CodingEntity();
        codingEntity.setId(codingDTO.getId());
        codingEntity.setUserId(codingDTO.getUserID());
        codingEntity.setCodingtitle(codingDTO.getCodingTitle());
        codingEntity.setCodingcontents(codingDTO.getCodingContents());
        codingEntity.setCodinghashtag(codingDTO.getCodingHashtag());
        codingEntity.setCodingtype(codingDTO.getCodingType());
        return codingEntity;
    }

    public static CodingEntity toSaveFileEntity(CodingDTO codingDTO) {
        CodingEntity codingEntity = new CodingEntity();
        codingEntity.setUserId(codingDTO.getUserID());
        codingEntity.setCodingtitle(codingDTO.getCodingTitle());
        codingEntity.setCodingcontents(codingDTO.getCodingContents());
        codingEntity.setCodinghashtag(codingDTO.getCodingHashtag());
        codingEntity.setCodingtype(codingDTO.getCodingType());
        codingEntity.setFileAttached(1);
        return codingEntity;
    }
}
