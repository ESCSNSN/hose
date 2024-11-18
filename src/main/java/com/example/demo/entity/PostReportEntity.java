package com.example.demo.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_reports")
@Getter
@Setter
public class PostReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;
    private String reporterId;
    private String reason;
    private LocalDateTime reportedAt;

    // 기본 생성자
    public PostReportEntity() {}

    // 신고 정보를 설정하는 생성자
    public PostReportEntity(Long postId, String reporterId, String reason) {
        this.postId = postId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.reportedAt = LocalDateTime.now();
    }
}
