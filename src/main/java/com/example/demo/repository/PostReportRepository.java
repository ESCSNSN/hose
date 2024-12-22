package com.example.demo.repository;

import com.example.demo.entity.PostReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

public interface PostReportRepository extends JpaRepository<PostReportEntity, Long> {
    // 특정 게시물과 신고자 ID로 신고 검색
    Optional<PostReportEntity> findByPostIdAndReporterId(Long postId, String reporterId);
    List<PostReportEntity> findByPostId(Long postId);
    void deleteByPostId (Long postId);
}
