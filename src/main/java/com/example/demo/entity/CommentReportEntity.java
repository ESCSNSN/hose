package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comment_reports")
public class CommentReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long commentId; // 신고 대상 댓글 ID

    @Column(length = 20, nullable = false)
    private String reporterId; // 신고한 사용자 ID

    @Column(length = 500, nullable = false)
    private String reason; // 신고 사유

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime reportedAt;

    // 생성 시 시간 자동 설정
    @PrePersist
    protected void onCreate() {
        this.reportedAt = LocalDateTime.now();
    }
}
