package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "graduate_file_table")
public class GraduateFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String originalFilename;

    @Column
    private String storedFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graduate_id")
    private GraduateEntity graduateEntity;

    public static GraduateFileEntity toGraduateFileEntity(GraduateEntity graduateEntity, String originalFilename, String storedFilename) {
        GraduateFileEntity graduateFileEntity = new GraduateFileEntity();
        graduateFileEntity.setOriginalFilename(originalFilename);
        graduateFileEntity.setStoredFilename(storedFilename);
        graduateFileEntity.setGraduateEntity(graduateEntity);
        return graduateFileEntity;
    }
}
