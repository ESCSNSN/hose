package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentReportDTO {
    private Long commentId; // 신고 대상 댓글 ID
    private String boardType;    // 게시판 ID
    private Long postId;     // 게시글 ID
    private String reporterId; // 신고자 ID
    private String reason;   // 신고 사유
    private Integer reportCount; // 신고 횟수
}
