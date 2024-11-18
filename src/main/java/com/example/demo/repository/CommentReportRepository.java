package com.example.demo.repository;

import com.example.demo.entity.CommentReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReportEntity, Long> {

    // 같은 사용자, 같은 댓글에 대한 신고가 있는지 확인
    Optional<CommentReportEntity> findByCommentIdAndReporterId(Long commentId, String reporterId);
    List<CommentReportEntity> findByCommentId(Long commentId);
}
