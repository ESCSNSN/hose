package com.example.demo.dto;

import com.example.demo.entity.NoticeEntity;
import com.example.demo.entity.NoticeFileEntity;
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
    private String NoticeTitle;
    private String NoticeContents;
    private LocalDateTime NoticeCreatedTime;
    private LocalDateTime NoticeUpdatedTime;

    //파일 첨부를 위한 부분
    private List<MultipartFile> NoticeFile;
    private List<String> originalFileName;
    private List<String> storedFileName;
    private int fileAttached;

    public NoticeDTO(Long id, String userId, String noticeTitle, LocalDateTime noticeCreatedTime) {
        this.id = id;
        this.userId = userId;
        this.NoticeTitle = noticeTitle;
        this.NoticeCreatedTime = noticeCreatedTime;

    }

    public static NoticeDTO toNoticeDTO(NoticeEntity noticeEntity) {
        NoticeDTO noticeDTO = new NoticeDTO();
        noticeDTO.setId(noticeEntity.getId());
        noticeDTO.setUserId(noticeEntity.getUserId());
        noticeDTO.setNoticeTitle(noticeEntity.getNoticeTitle());
        noticeDTO.setNoticeContents(noticeEntity.getNoticeContents());
        noticeDTO.setNoticeCreatedTime(noticeEntity.getNoticeCreatedTime());
        noticeDTO.setNoticeUpdatedTime(noticeEntity.getNoticeUpdatedTime());
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
