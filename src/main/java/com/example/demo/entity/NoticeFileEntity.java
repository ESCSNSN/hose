package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "notice_file_table")
public class NoticeFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String originalFilename;

    @Column
    private String storedFilename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private NoticeEntity noticeEntity;

    public static NoticeFileEntity toNoticeFileEntity(NoticeEntity noticeEntity, String originalFilename, String storedFilename) {
        NoticeFileEntity noticeFileEntity = new NoticeFileEntity();
        noticeFileEntity.setOriginalFilename(originalFilename);
        noticeFileEntity.setStoredFilename(storedFilename);
        noticeFileEntity.setNoticeEntity(noticeEntity);
        return noticeFileEntity;
    }

}
