package com.example.demo.service;

import com.example.demo.dto.PostReportDTO;
import com.example.demo.entity.PostReportEntity;
import com.example.demo.repository.PostReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostReportService {

    private final PostReportRepository postReportRepository;

    /**
     * 게시물 신고 추가 또는 업데이트
     */
    @Transactional
    public void addReport(String boardType, Long postId, String reporterId, String reason) {
        // 기존 신고 확인
        Optional<PostReportEntity> existingReportOpt = postReportRepository.findByPostIdAndReporterId(postId, reporterId);

        if (existingReportOpt.isPresent()) {
            // 기존 신고가 있으면 카운트 증가 및 사유 업데이트
            PostReportEntity existingReport = existingReportOpt.get();
            existingReport.setReportCount(existingReport.getReportCount() + 1);
            existingReport.setReason(reason); // 사유 업데이트
            postReportRepository.save(existingReport);
        } else {
            // 신고가 없으면 새로 생성
            PostReportEntity newReport = new PostReportEntity();
            newReport.setBoardType(boardType);
            newReport.setPostId(postId);
            newReport.setReporterId(reporterId);
            newReport.setReason(reason);
            newReport.setReportCount(1);
            postReportRepository.save(newReport);
        }
    }

    /**
     * 모든 게시물 신고 목록 조회
     * @return 게시물 신고 DTO 리스트
     */

    public List<PostReportDTO> getAllReports(){
        return postReportRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 게시물 신고 목록 조회
     *
     * @param postId 게시물 ID
     * @return 특정 게시물 신고 DTO 리스트
     */
    public List<PostReportDTO> getReportsByPostId(Long postId) {
        return postReportRepository.findByPostId(postId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 엔티티를 DTO로 변환
     *
     * @param entity 게시물 신고 엔티티
     * @return 게시물 신고 DTO
     */
    private PostReportDTO convertToDTO(PostReportEntity entity) {
        return new PostReportDTO(
                entity.getPostId(),
                entity.getReporterId(),
                entity.getReason(),
                entity.getBoardType(),
                entity.getReportCount()
        );
    }

}

