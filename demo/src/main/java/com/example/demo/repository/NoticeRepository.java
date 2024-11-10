package com.example.demo.repository;

import com.example.demo.entity.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;


public interface NoticeRepository extends JpaRepository<NoticeEntity, Long> {



            // 고정된 항목을 상단에 두고 최신순 정렬
            Page<NoticeEntity> findAllByOrderByIsPinnedDescNoticeCreatedTimeDesc(Pageable pageable);

            // 검색 시 고정된 항목을 상단에 두고 최신순 정렬
            @Query("SELECT n FROM NoticeEntity n WHERE " +
                    "(:title IS NULL OR :title = '' OR n.noticeTitle LIKE %:title%) AND " +
                    "(:content IS NULL OR :content = '' OR n.noticeContents LIKE %:content%) " +
                    "ORDER BY n.isPinned DESC, n.noticeCreatedTime DESC")
            Page<NoticeEntity> findByTitleOrContentsContaining(
                    @Param("title") String title,
                    @Param("content") String content,
                    Pageable pageable
            );

}









