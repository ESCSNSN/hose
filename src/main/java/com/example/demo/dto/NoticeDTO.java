package com.example.demo.dto;

import com.example.demo.entity.NoticeEntity;
import com.example.demo.entity.NoticeFileEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDTO {
    private Long id;
    private String userId;
    private String noticeTitle;
    private String noticeContents;
    private LocalDateTime noticeCreatedTime;
    private LocalDateTime noticeUpdatedTime;
    private boolean isPinned; // 고정 여부 추가

    //파일 첨부를 위한 부분
    @JsonIgnore
    private List<MultipartFile> noticeFile;
    private List<String> originalFileName;
    private List<String> storedFileName;
    private int fileAttached;

    public NoticeDTO(Long id, String userId, String noticeTitle, LocalDateTime noticeCreatedTime,boolean isPinned) {
        this.id = id;
        this.userId = userId;
        this.noticeTitle = noticeTitle;
        this.noticeCreatedTime = noticeCreatedTime;
        this.isPinned = isPinned;

    }

    public static NoticeDTO toNoticeDTO(NoticeEntity noticeEntity) {
        NoticeDTO noticeDTO = new NoticeDTO();
        noticeDTO.setId(noticeEntity.getId());
        noticeDTO.setUserId(noticeEntity.getUserId());
        noticeDTO.setNoticeTitle(noticeEntity.getNoticeTitle());
        noticeDTO.setNoticeContents(noticeEntity.getNoticeContents());
        noticeDTO.setNoticeCreatedTime(noticeEntity.getNoticeCreatedTime());
        noticeDTO.setNoticeUpdatedTime(noticeEntity.getNoticeUpdatedTime());
        noticeDTO.setPinned(noticeEntity.isPinned());
        if(noticeEntity.getFileAttached() == 0) {
            noticeDTO.setFileAttached(noticeEntity.getFileAttached());
        }
        else {
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            noticeDTO.setFileAttached(noticeEntity.getFileAttached());

            for(NoticeFileEntity noticeFileEntity: noticeEntity.getNoticeFileEntityList()) {
              originalFileNameList.add(noticeFileEntity.getOriginalFilename());
              storedFileNameList.add(noticeFileEntity.getStoredFilename());
            }
            noticeDTO.setOriginalFileName(originalFileNameList);
            noticeDTO.setStoredFileName(storedFileNameList);
        }
        return noticeDTO;
    }

}
