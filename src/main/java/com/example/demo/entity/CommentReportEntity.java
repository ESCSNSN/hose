package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comment_reports", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"comment_id", "reporter_id"})
})
public class CommentReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 연관된 댓글 엔티티와의 관계 설정
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentEntity comment;

    @Column(length = 30)
    private String boardType; // 게시판 ID

    @Column(nullable = false)
    private Long postId; // 게시글 ID

    @Column(length = 20, nullable = false)
    private String reporterId; // 신고한 사용자 ID

    @Column(length = 500, nullable = false)
    private String reason; // 신고 사유

    @Column(nullable = false)
    private Integer reportCount = 1; // 신고 횟수

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime reportedAt;

    // 생성 시 시간 자동 설정
    @PrePersist
    protected void onCreate() {
        this.reportedAt = LocalDateTime.now();
    }
}
