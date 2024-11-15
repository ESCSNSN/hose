package com.example.demo.dto;

import com.example.demo.entity.CommentReportEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentReportDTO {
    private Long id;
    private Long commentId;
    private String reporterId;
    private String reason;
    private LocalDateTime reportedAt;

    // 기본 생성자
    public CommentReportDTO() {}

    // 엔티티를 DTO로 변환하는 생성자
    public CommentReportDTO(CommentReportEntity entity) {
        this.id = entity.getId();
        this.commentId = entity.getCommentId();
        this.reporterId = entity.getReporterId();
        this.reason = entity.getReason();
        this.reportedAt = entity.getReportedAt();
    }

    // 새 댓글 신고를 위한 생성자
    public CommentReportDTO(Long commentId, String reporterId, String reason) {
        this.commentId = commentId;
        this.reporterId = reporterId;
        this.reason = reason;
    }
}
