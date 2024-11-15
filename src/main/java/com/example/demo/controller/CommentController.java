package com.example.demo.controller;

import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.CommentReportDTO;
import com.example.demo.exception.UnauthorizedDeletionException;
import com.example.demo.service.CommentReportService;
import com.example.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentReportService commentReportService;

    /**
     * 댓글 추가 (일반 댓글 및 대댓글)
     * URL: POST /comments/add
     */
    @PostMapping("/add")
    public String addComment(@ModelAttribute CommentDTO commentDTO) {
        commentService.addComment(commentDTO);
        return "redirect:/board/" + commentDTO.getTargetType() + "/" + commentDTO.getTargetId();
    }

    /**
     * 댓글 삭제
     * URL: POST /comments/delete/{id}
     */
    @PostMapping("/delete/{id}")
    public String deleteComment(@PathVariable Long id,
                                @RequestParam String targetType,
                                @RequestParam Long targetId,
                                @RequestParam String userId) {
        try {
            commentService.deleteComment(id, userId);
        } catch (UnauthorizedDeletionException e) {
            // 권한이 없는 경우 처리 (예: 에러 메시지 표시)
            return "redirect:/board/" + targetType + "/" + targetId + "?error=" + e.getMessage();
        } catch (IllegalArgumentException e) {
            // 댓글이 존재하지 않는 경우 처리
            return "redirect:/board/" + targetType + "/" + targetId + "?error=" + e.getMessage();
        }
        return "redirect:/board/" + targetType + "/" + targetId;
    }

    /**
     * 댓글 신고
     * URL: POST /comments/report
     */
    @PostMapping("/report")
    public String reportComment(@ModelAttribute CommentReportDTO reportDTO,
                                @RequestParam String targetType,
                                @RequestParam Long targetId) {
        commentReportService.addReport(reportDTO);
        return "redirect:/board/" + targetType + "/" + targetId + "?report_success=true";
    }
}
