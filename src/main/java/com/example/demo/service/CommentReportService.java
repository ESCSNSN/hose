package com.example.demo.service;

import com.example.demo.dto.CommentReportDTO;
import com.example.demo.entity.CommentReportEntity;
import com.example.demo.entity.CommentEntity;
import com.example.demo.repository.CommentReportRepository;
import com.example.demo.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentReportService {

    private final CommentReportRepository commentReportRepository;
    private final CommentRepository commentRepository;
    private static final Logger logger = LoggerFactory.getLogger(CommentReportService.class);

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
        Optional<CommentReportEntity> existingReportOpt = commentReportRepository.findByCommentIdAndReporterId(reportDTO.getCommentId(), reportDTO.getReporterId());

        if (existingReportOpt.isPresent()) {
            // 신고가 이미 존재하면 카운트 증가 및 사유 업데이트
            CommentReportEntity existingReport = existingReportOpt.get();
            existingReport.setReportCount(existingReport.getReportCount() + 1);
            existingReport.setReason(reportDTO.getReason()); // 사유 업데이트 (선택 사항)
            commentReportRepository.save(existingReport);
        } else {
            // 신고가 없으면 새로 생성
            CommentReportEntity report = new CommentReportEntity();
            report.setComment(comment);
            report.setBoardType(reportDTO.getBoardType());
            report.setPostId(reportDTO.getPostId());
            report.setReporterId(reportDTO.getReporterId());
            report.setReason(reportDTO.getReason());
            report.setReportCount(1);
            commentReportRepository.save(report);
        }
    }

    /**
     * 모든 댓글 신고 목록 조회
     *
     * @return 댓글 신고 DTO 리스트
     */
    public List<CommentReportDTO> getAllReports() {
        return commentReportRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 댓글의 신고 목록 조회
     *
     * @param commentId 댓글 ID
     * @return 댓글 신고 DTO 리스트
     */
    public List<CommentReportDTO> getReportsByCommentId(Long commentId) {
        return commentReportRepository.findByCommentId(commentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void rejectAllReportsByCommentId(Long commentId) {
        List<CommentReportEntity> reports = commentReportRepository.findByCommentId(commentId);
        if (reports.isEmpty()) {
            throw new IllegalArgumentException("이 댓글에 대한 신고가 없습니다.: " + commentId);
        }
        commentReportRepository.deleteByCommentId(commentId);
        logger.info("댓글 ID={}에 대한 모든 신고를 삭제했습니다.", commentId);

    }

    /**
     * 엔티티를 DTO로 변환
     *
     * @param entity 댓글 신고 엔티티
     * @return 댓글 신고 DTO
     */
    private CommentReportDTO convertToDTO(CommentReportEntity entity) {
        return new CommentReportDTO(
                entity.getComment().getId(),
                entity.getBoardType(),
                entity.getPostId(),
                entity.getReporterId(),
                entity.getReason(),
                entity.getReportCount()
        );
    }

    // 추가적인 서비스 메서드들...
}
