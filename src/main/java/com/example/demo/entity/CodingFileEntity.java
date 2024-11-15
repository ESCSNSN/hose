package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "coding_file_table")
public class CodingFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String originalFileName;

    @Column
    private String storedFileName; // 오타 수정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coding_id") // 외래 키 컬럼명 설정
    private CodingEntity codingEntity;

    public static CodingFileEntity toCodingFileEntity(CodingEntity codingEntity, String originalFileName, String storedFileName) {
        CodingFileEntity codingFileEntity = new CodingFileEntity();
        codingFileEntity.setOriginalFileName(originalFileName);
        codingFileEntity.setStoredFileName(storedFileName); // 오타 수정
        codingFileEntity.setCodingEntity(codingEntity);
        return codingFileEntity;
    }
}
