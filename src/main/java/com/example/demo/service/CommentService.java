package com.example.demo.service;

import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.CommentReportDTO;
import com.example.demo.entity.CommentEntity;
import com.example.demo.exception.UnauthorizedDeletionException;
import com.example.demo.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentReportService commentReportService;

    /**
     * 댓글 추가 (일반 댓글 및 대댓글)
     *
     * @param commentDTO 댓글 데이터
     */
    public void addComment(CommentDTO commentDTO) {
        CommentEntity comment = new CommentEntity();
        comment.setContent(commentDTO.getContent());
        comment.setUserId(commentDTO.getUserId());
        comment.setTargetType(commentDTO.getTargetType());
        comment.setTargetId(commentDTO.getTargetId());

        if (commentDTO.getParentCommentId() != null) {
            CommentEntity parentComment = commentRepository.findById(commentDTO.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid parent comment ID"));
            comment.setParentComment(parentComment);
        }

        commentRepository.save(comment);
    }

    /**
     * 댓글 삭제
     *
     * @param commentId 삭제할 댓글 ID
     * @param userId    현재 사용자 ID
     */
    public void deleteComment(Long commentId, String userId) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));

        if (!comment.getUserId().equals(userId)) {
            throw new UnauthorizedDeletionException("You do not have permission to delete this comment.");
        }

        commentRepository.delete(comment);
    }

    /**
     * 특정 게시판의 댓글 목록 가져오기 (대댓글 포함)
     *
     * @param targetType 게시판 유형
     * @param targetId   게시판 ID
     * @param pageable   페이징 정보
     * @return 댓글 DTO 페이지
     */
    public Page<CommentDTO> getComments(String targetType, Long targetId, Pageable pageable) {
        Page<CommentEntity> commentsPage = commentRepository.findByTargetTypeAndTargetId(targetType, targetId, pageable);
        return commentsPage.map(CommentDTO::new);
    }

    /**
     * 댓글 소유자 검증
     *
     * @param commentId 댓글 ID
     * @param username  사용자 이름
     * @return 소유자 여부
     */
    public boolean isOwner(Long commentId, String username) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
        return comment.getUserId().equals(username);
    }

    /**
     * 댓글 신고
     *
     * @param commentId  신고 대상 댓글 ID
     * @param reporterId 신고자 ID
     * @param reason     신고 사유
     */
    public void reportComment(Long commentId, String reporterId, String reason) {
        CommentReportDTO reportDTO = new CommentReportDTO(commentId, reporterId, reason);
        commentReportService.addReport(reportDTO);
    }
}