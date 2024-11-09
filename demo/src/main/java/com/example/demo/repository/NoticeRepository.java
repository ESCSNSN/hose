package com.example.demo.repository;

import com.example.demo.entity.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {




    @Query(value = "SELECT * FROM notice_table WHERE " +
            "(:title IS NULL OR :title = '' OR notice_title LIKE CONCAT('%', :title, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:content IS NULL OR :content = '' OR notice_contents LIKE CONCAT('%', :content, '%') COLLATE utf8mb4_unicode_ci)",
            nativeQuery = true)
    Page<NoticeEntity> findByTitleOrContentsContaining(@Param("title") String title, @Param("content") String content, Pageable pageable);


}


