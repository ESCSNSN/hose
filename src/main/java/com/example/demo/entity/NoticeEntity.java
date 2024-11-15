package com.example.demo.entity;

import com.example.demo.dto.NoticeDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "notice_table")
public class NoticeEntity extends NoticeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 20, nullable = false)
    private String userId;

    @Column(name = "notice_title")
    private String noticeTitle;  // camelCase로 수정

    @Column(name = "notice_contents",length = 500)
    private String noticeContents;  // camelCase로 수정

    @Column
    private int fileAttached; // 1 or 0

    @Column(name = "is_pinned", nullable = false)
    private boolean isPinned = false;  // 고정 여부를 나타내는 필드, 기본값 false

    @OneToMany(mappedBy = "noticeEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NoticeFileEntity> noticeFileEntityList = new ArrayList<>();

    public static NoticeEntity toSaveEntity(NoticeDTO noticeDTO) {
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.setUserId(noticeDTO.getUserId());
        noticeEntity.setNoticeTitle(noticeDTO.getNoticeTitle());
        noticeEntity.setNoticeContents(noticeDTO.getNoticeContents());
        noticeEntity.setFileAttached(0);
        return noticeEntity;
    }

    public static NoticeEntity toUpdatedEntity(NoticeDTO noticeDTO) {
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.setId(noticeDTO.getId());
        noticeEntity.setUserId(noticeDTO.getUserId());
        noticeEntity.setNoticeTitle(noticeDTO.getNoticeTitle());
        noticeEntity.setNoticeContents(noticeDTO.getNoticeContents());
        return noticeEntity;
    }

    public static NoticeEntity toSaveFileEntity(NoticeDTO noticeDTO) {
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.setUserId(noticeDTO.getUserId());
        noticeEntity.setNoticeTitle(noticeDTO.getNoticeTitle());
        noticeEntity.setNoticeContents(noticeDTO.getNoticeContents());
        noticeEntity.setFileAttached(1);
        return noticeEntity;
    }

    // Getters and Setters 추가
    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        this.isPinned = pinned;
    }

}
