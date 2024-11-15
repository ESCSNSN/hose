package com.example.demo.service;

import com.example.demo.dto.CommentReportDTO;
import com.example.demo.entity.CommentReportEntity;
import com.example.demo.repository.CommentReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentReportService {

    private final CommentReportRepository commentReportRepository;

    /**
     * 댓글 신고 추가
     *
     * @param reportDTO 댓글 신고 데이터
     */
    public void addReport(CommentReportDTO reportDTO) {
        CommentReportEntity report = new CommentReportEntity();
        report.setCommentId(reportDTO.getCommentId());
        report.setReporterId(reportDTO.getReporterId());
        report.setReason(reportDTO.getReason());
        commentReportRepository.save(report);
    }
}
