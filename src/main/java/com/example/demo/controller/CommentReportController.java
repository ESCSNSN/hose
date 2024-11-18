package com.example.demo.controller;

import com.example.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 댓글 신고를 처리하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class CommentReportController {

    private final CommentService commentService;

    /**
     * 댓글 신고
     * URL: POST /api/board/{boardType}/post/{postId}/comments/{commentId}/report
     *
     * @param boardType     게시판 종류
     * @param postId      게시글 ID
     * @param commentId   신고 대상 댓글 ID
     * @param reason      신고 사유
     * @param reporterId  신고자 ID
     * @return 신고 성공 메시지
     */
    @PostMapping("/{boardType}/post/{postId}/comments/{commentId}/report")
    public ResponseEntity<String> reportComment(
            @PathVariable String boardType,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestParam String reason,
            @RequestParam String reporterId) {

        commentService.reportComment(boardType, postId, commentId, reporterId, reason);
        return ResponseEntity.ok("댓글이 성공적으로 신고되었습니다.");
    }
}
