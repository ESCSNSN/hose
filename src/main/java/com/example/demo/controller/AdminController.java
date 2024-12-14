package com.example.demo.controller;

import com.example.demo.dto.CommentReportDTO;
import com.example.demo.dto.PostReportDTO;
import com.example.demo.service.CommentReportService;
import com.example.demo.service.PostReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자용 댓글 신고 관리 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final CommentReportService commentReportService;
    private final PostReportService postReportService;

    /**
     * 모든 댓글 신고 목록 조회
     * URL: GET /api/admin/comment-reports
     *
     * @return 모든 댓글 신고 DTO 리스트
     */
    @GetMapping("/comment-reports")
    public ResponseEntity<List<CommentReportDTO>> getAllCommentReports() {
        List<CommentReportDTO> reports = commentReportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    /**
     * 특정 댓글의 신고 목록 조회
     * URL: GET /api/admin/comment-reports/{commentId}
     *
     * @param commentId 댓글 ID
     * @return 특정 댓글에 대한 신고 DTO 리스트
     */
    @GetMapping("/comment-reports/{commentId}")
    public ResponseEntity<List<CommentReportDTO>> getCommentReportsByCommentId(@PathVariable Long commentId) {
        List<CommentReportDTO> reports = commentReportService.getReportsByCommentId(commentId);
        return ResponseEntity.ok(reports);
    }

    /**
     * 모든 게시물 신고 목록 조회
     * URL: GET /api/admin/post-reports
     *
     * @return 모든 게시물 신고 DTO 리스트
     */
    @GetMapping("/post-reports")
    public ResponseEntity<List<PostReportDTO>> getAllPostReports() {
        List<PostReportDTO> reports = postReportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    /**
     * 특정 게시물 신고 목록 조회
     * URL: GET /api/admin/post-reports/{postId}
     *
     * @param postId 게시물 ID
     * @return 특정 게시물 신고 DTO 리스트
     */

    @GetMapping("/post-reports/{postId}")
    public ResponseEntity<List<PostReportDTO>> getPostReportsByPostId(@PathVariable Long postId) {
        List<PostReportDTO> reports = postReportService.getReportsByPostId(postId);
        return ResponseEntity.ok(reports);
    }



}
