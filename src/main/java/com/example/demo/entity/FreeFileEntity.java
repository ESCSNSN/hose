package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "free_file_table")
public class FreeFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String originalFileName;

    @Column
    private String storedFileName; // 오타 수정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "free_id") // 외래 키 컬럼명 설정
    private FreeEntity freeEntity;

    public static FreeFileEntity toFreeFileEntity(FreeEntity freeEntity, String originalFileName, String storedFileName) {
        FreeFileEntity freeFileEntity = new FreeFileEntity();
        freeFileEntity.setOriginalFileName(originalFileName);
        freeFileEntity.setStoredFileName(storedFileName); // 오타 수정
        freeFileEntity.setFreeEntity(freeEntity);
        return freeFileEntity;
    }
}

