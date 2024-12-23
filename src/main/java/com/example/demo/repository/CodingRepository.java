package com.example.demo.repository;

import com.example.demo.entity.CodingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CodingRepository extends JpaRepository<CodingEntity, Long> {

    @Query(value = "SELECT * FROM coding_table WHERE " +
            "(:title IS NULL OR :title = '' OR coding_title LIKE CONCAT('%', :title, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:content IS NULL OR :content = '' OR coding_contents LIKE CONCAT('%', :content, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:hashtag IS NULL OR :hashtag = '' OR coding_hashtag LIKE CONCAT('%', :hashtag, '%') COLLATE utf8mb4_unicode_ci)"+
            "AND (:type IS NULL OR :type = '' OR coding_type LIKE CONCAT('%', :type, '%') COLLATE utf8mb4_unicode_ci)",
            nativeQuery = true)
    Page<CodingEntity> findByTitleOrContentsContaining(
            @Param("title") String title,
            @Param("content") String content,
            @Param("hashtag") String hashtag,
            @Param("type") String type,
            Pageable pageable);




    List<CodingEntity> findBycodingLikeGreaterThanEqualOrderByCodingCreatedTimeDesc(int codingLike, Pageable pageable);

    @Query("SELECT c FROM CodingEntity c LEFT JOIN FETCH c.codingFileEntityList ORDER BY c.codingCreatedTime DESC")
    List<CodingEntity> findTop2CodingsWithFiles(PageRequest pageable);
}
