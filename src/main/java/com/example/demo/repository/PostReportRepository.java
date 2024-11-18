package com.example.demo.repository;

import com.example.demo.entity.PostReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReportRepository extends JpaRepository<PostReportEntity, Long> {
    // 추가적인 쿼리가 필요하다면 여기에 작성
}
