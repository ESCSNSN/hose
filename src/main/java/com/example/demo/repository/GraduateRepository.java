package com.example.demo.repository;

import com.example.demo.entity.FreeEntity;
import com.example.demo.entity.GraduateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GraduateRepository extends JpaRepository<GraduateEntity,Long> {

    @Query(value = "SELECT * FROM graduate_table WHERE " +
            "(:graduateId IS NULL OR :graduateId = '' OR graduate_id LIKE CONCAT('%', :graduateId, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:title IS NULL OR :title = '' OR graduate_title LIKE CONCAT('%', :title, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:content IS NULL OR :content = '' OR graduate_contents LIKE CONCAT('%', :content, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:hashtag IS NULL OR :hashtag = '' OR graduate_hashtag LIKE CONCAT('%', :hashtag, '%') COLLATE utf8mb4_unicode_ci)",
            nativeQuery = true)
    Page<GraduateEntity> findByTitleOrContentsContaining(
            @Param("graduateId") String graduateId,
            @Param("title") String title,
            @Param("content") String content,
            @Param("hashtag") String hashtag,
            Pageable pageable);

    List<GraduateEntity> findByGraduateLikeGreaterThanEqualOrderByGraduateCreatedTimeDesc(int graduateLike, Pageable pageable);
    List<GraduateEntity> findByUserId(String userId);

    @Query("SELECT g FROM GraduateEntity g WHERE g.graduateId = :graduateId ORDER BY g.graduateCreatedTime DESC")
    List<GraduateEntity> findTop3GraduatesByGraduateId(@Param("graduateId") String graduateId, Pageable pageable);
}
