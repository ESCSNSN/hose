package com.example.demo.repository;

import com.example.demo.entity.CompetitionEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CompetitionRepository extends JpaRepository<CompetitionEntity, Long> {

    @Query(value = "SELECT * FROM competition_table WHERE " +
            "(:title IS NULL OR :title = '' OR competition_title LIKE CONCAT('%', :title, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:content IS NULL OR :content = '' OR competition_contents LIKE CONCAT('%', :content, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:hashtag IS NULL OR :hashtag = '' OR competition_hashtag LIKE CONCAT('%', :hashtag, '%') COLLATE utf8mb4_unicode_ci)",
            nativeQuery = true)
    Page<CompetitionEntity> findByTitleOrContentsContaining(
            @Param("title") String title,
            @Param("content") String content,
            @Param("hashtag") String hashtag,
            Pageable pageable);
}