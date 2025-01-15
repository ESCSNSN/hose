package com.example.demo.dto;

import com.example.demo.entity.PostReportEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostReportDTO {
    private Long postId;
    private String reporterId;
    private String reason;
    private String boardType;
    private int reportCount;
    private String reportId;

    // 생성자, getter, setter
    public PostReportDTO(Long postId, String reporterId, String reason, String boardType, int reportCount, String reportId) {
        this.postId = postId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.boardType = boardType;
        this.reportCount = reportCount;
        this.reportId = reportId;
    }
}
