package com.example.demo.controller;

import com.example.demo.dto.PostReportDTO;
import com.example.demo.service.PostReportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
public class PostReportApiController {

    @Autowired
    private PostReportService postReportService;

    /**
     * POST /api/board/{boardType}/{postId}/report
     */
    @PostMapping("/{boardType}/{postId}/report")
    public ResponseEntity<String> reportPost(@PathVariable Long postId,
                                             @PathVariable String boardType,
                                             @RequestParam String reason,
                                             HttpServletRequest request) {
        String reporterId = (String) request.getAttribute("username");
        postReportService.addReport(boardType,postId,reporterId, reason);
        return ResponseEntity.ok("게시글이 성공적으로 신고되었습니다.");
    }
}
