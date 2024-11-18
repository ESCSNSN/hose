package com.example.demo.service;

import com.example.demo.dto.CommentReportDTO;
import com.example.demo.entity.CommentReportEntity;
import com.example.demo.entity.CommentEntity;
import com.example.demo.repository.CommentReportRepository;
import com.example.demo.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CommentReportService {

    private final CommentReportRepository commentReportRepository;
    private final CommentRepository commentRepository;

    /**
     * 댓글 신고 추가 또는 업데이트
     *
     * @param reportDTO 댓글 신고 데이터
     */
    @Transactional
    public void addReport(CommentReportDTO reportDTO) {
        // 댓글 엔티티 조회
        CommentEntity comment = commentRepository.findById(reportDTO.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

        // 기존 신고 조회
        commentReportRepository.findByCommentIdAndReporterId(reportDTO.getCommentId(), reportDTO.getReporterId())
                .ifPresentOrElse(existingReport -> {
                    // 신고가 이미 존재하면 카운트 증가
                    existingReport.setReportCount(existingReport.getReportCount() + 1);
                    existingReport.setReason(reportDTO.getReason()); // 사유 업데이트 (선택 사항)
                    commentReportRepository.save(existingReport);
                }, () -> {
                    // 신고가 없으면 새로 생성
                    CommentReportEntity report = new CommentReportEntity();
                    report.setComment(comment);
                    report.setBoardType(reportDTO.getBoardType());
                    report.setPostId(reportDTO.getPostId());
                    report.setReporterId(reportDTO.getReporterId());
                    report.setReason(reportDTO.getReason());
                    report.setReportCount(1);
                    commentReportRepository.save(report);
                });
    }
}
