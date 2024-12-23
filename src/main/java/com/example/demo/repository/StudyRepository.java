package com.example.demo.repository;

import com.example.demo.entity.QuestEntity;
import com.example.demo.entity.StudyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyRepository extends JpaRepository<StudyEntity, Long> {

    @Query(value = "SELECT * FROM study_table WHERE " +
            "(:studyid IS NULL OR :studyid = '' OR study_id LIKE CONCAT('%', :studyid, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:title IS NULL OR :title = '' OR study_title LIKE CONCAT('%', :title, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:content IS NULL OR :content = '' OR study_contents LIKE CONCAT('%', :content, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:hashtag IS NULL OR :hashtag = '' OR study_hashtag LIKE CONCAT('%', :hashtag, '%') COLLATE utf8mb4_unicode_ci)",
            nativeQuery = true)
    Page<StudyEntity> findByTitleOrContentsContaining(
            @Param("studyid") String studyid,
            @Param("title") String title,
            @Param("content") String content,
            @Param("hashtag") String hashtag,
            Pageable pageable);


    @Query(value = "SELECT * FROM study_table WHERE " +
            "(:studyid IS NULL OR :studyid = '' OR study_id LIKE CONCAT('%', :studyid, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:title IS NULL OR :title = '' OR study_title LIKE CONCAT('%', :title, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:content IS NULL OR :content = '' OR study_contents LIKE CONCAT('%', :content, '%') COLLATE utf8mb4_unicode_ci) " +
            "AND (:hashtag IS NULL OR :hashtag = '' OR study_hashtag LIKE CONCAT('%', :hashtag, '%') COLLATE utf8mb4_unicode_ci) " +
            "ORDER BY deadline ASC",
            nativeQuery = true)
    Page<StudyEntity> searchStudiesByFilters(
            @Param("studyid") String studyid,
            @Param("title") String title,
            @Param("content") String content,
            @Param("hashtag") String hashtag,
            Pageable pageable);




    List<StudyEntity> findByStudyLikeGreaterThanEqualOrderByStudyCreatedTimeDesc(int studyLike, Pageable pageable);

    @Query("SELECT DISTINCT s FROM StudyEntity s LEFT JOIN FETCH s.studyFileEntityList WHERE s.studyId = :studyID ORDER BY s.studyCreatedTime DESC, s.id DESC")
    List<StudyEntity> findTopStudiesByStudyId(String studyID, Pageable pageable);


}
