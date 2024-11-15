package com.example.demo.repository;

import com.example.demo.entity.CommentReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportRepository extends JpaRepository<CommentReportEntity, Long> {
    // 추가적인 조회 메서드가 필요하면 여기에 정의
}
