package com.example.demo.repository;

import com.example.demo.entity.CodingEntity;
import com.example.demo.entity.FreeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FreeRepository extends JpaRepository<FreeEntity, Long> {

    @Query(value = "SELECT * FROM free_table WHERE " +
            "(:title IS NULL OR :title = '' OR free_title LIKE CONCAT('%', :title, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:content IS NULL OR :content = '' OR free_contents LIKE CONCAT('%', :content, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:hashtag IS NULL OR :hashtag = '' OR free_hashtag LIKE CONCAT('%', :hashtag, '%') COLLATE utf8mb4_unicode_ci)",

            nativeQuery = true)
    Page<FreeEntity> findByTitleOrContentsContaining(
            @Param("title") String title,
            @Param("content") String content,
            @Param("hashtag") String hashtag,
            Pageable pageable);




    List<FreeEntity> findByFreeLikeGreaterThanEqualOrderByFreeCreatedTimeDesc(int freeLike, Pageable pageable);

    @Query("SELECT f FROM FreeEntity f LEFT JOIN FETCH f.freeFileEntityList ORDER BY f.freeCreatedTime DESC")
    List<FreeEntity> findTop3FreePostsWithFiles(Pageable pageable);
}
