package com.example.demo.repository;

import com.example.demo.entity.QuestScrapEntity;
import com.example.demo.entity.StudyScrapEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyScrapRepository extends JpaRepository<StudyScrapEntity,Long> {
    boolean existsByUserIdAndStudyEntityId(String userId, Long studyEntityId);
    Optional<StudyScrapEntity> findByUserIdAndStudyEntityId(String userId, Long studyEntityId);
    List<StudyScrapEntity> findByUserId(String userId);

    @Query("SELECT s FROM StudyScrapEntity s WHERE s.userId = :userId AND s.studyEntity.id < :lastStudyId" +
            " AND (:studyId IS NULL OR s.studyEntity.studyId = :studyId)" +
            " ORDER BY s.studyEntity.id DESC")
    List<StudyScrapEntity> findByUserIdAndStudyEntityIdLessThanOrderByStudyEntityIdDesc(
            @Param("userId") String userId,
            @Param("lastStudyId") Long lastStudyId,
            @Param("studyId") String studyId,
            Pageable pageable);

    @Query("SELECT s FROM StudyScrapEntity s WHERE s.userId = :userId" +
            " AND (:studyId IS NULL OR s.studyEntity.studyId = :studyId)" +
            " ORDER BY s.studyEntity.id DESC")
    List<StudyScrapEntity> findByUserIdOrderByStudyEntityIdDesc(
            @Param("userId") String userId,
            @Param("studyId") String studyId,
            Pageable pageable);

}
