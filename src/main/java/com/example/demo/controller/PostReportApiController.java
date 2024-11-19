package com.example.demo.controller;

import com.example.demo.dto.PostReportDTO;
import com.example.demo.service.PostReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
public class PostReportApiController {

    @Autowired
    private PostReportService postReportService;

    /**
     * POST /api/posts/{id}/report
     */
    @PostMapping("/{id}/report")
    public ResponseEntity<String> reportPost(@PathVariable Long id,
                                             @RequestParam String reason,
                                             @RequestParam String reporterId) {
        PostReportDTO reportDTO = new PostReportDTO(id, reporterId, reason);
        postReportService.addReport(reportDTO);
        return ResponseEntity.ok("게시글이 성공적으로 신고되었습니다.");
    }
}
