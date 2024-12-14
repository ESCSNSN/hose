package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "post_reports")
public class PostReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId; // 게시물 ID

    @Column(nullable = false)
    private String reporterId; // 신고자 ID

    @Column(nullable = false, length = 500)
    private String reason; // 신고 사유

    @Column(nullable = false)
    private String boardType; // 게시판 유형

    @Column(nullable = false)
    private int reportCount = 0; // 신고 횟수
}

