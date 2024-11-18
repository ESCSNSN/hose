package com.example.demo.dto;

import com.example.demo.entity.PostReportEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostReportDTO {
    private Long id;
    private Long postId;
    private String reporterId;
    private String reason;
    private LocalDateTime reportedAt;

    // 기본 생성자
    public PostReportDTO() {}

    // 엔티티를 DTO로 변환하는 생성자
    public PostReportDTO(PostReportEntity entity) {
        this.id = entity.getId();
        this.postId = entity.getPostId();
        this.reporterId = entity.getReporterId();
        this.reason = entity.getReason();
        this.reportedAt = entity.getReportedAt();
    }

    // 새 게시글 신고를 위한 생성자
    public PostReportDTO(Long postId, String reporterId, String reason) {
        this.postId = postId;
        this.reporterId = reporterId;
        this.reason = reason;
    }
}
