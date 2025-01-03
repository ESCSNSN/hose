package com.example.demo.repository;

import com.example.demo.entity.GraduateScrapEntity;
import com.example.demo.entity.StudyScrapEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GraduateScrapRepository extends JpaRepository<GraduateScrapEntity,Long> {

    boolean existsByUserIdAndGraduateEntityId(String userId, Long graduateEntityId);
    Optional<GraduateScrapEntity> findByUserIdAndGraduateEntityId(String userId, Long graduateEntityId);
    List<GraduateScrapEntity> findByUserId(String userId);

    @Query("SELECT s FROM GraduateScrapEntity s WHERE s.userId = :userId AND s.graduateEntity.id < :lastGraduateId" +
            " AND (:graduateId IS NULL OR s.graduateEntity.graduateId = :graduateId)" +
            " ORDER BY s.graduateEntity.id DESC")
    List<GraduateScrapEntity> findByUserIdAndGraduateEntityIdLessThanOrderByGraduateEntityIdDesc(
            @Param("userId") String userId,
            @Param("lastGraduateId") Long lastGraduateId,
            @Param("graduateId") String graduateId,
            Pageable pageable);

    @Query("SELECT s FROM GraduateScrapEntity s WHERE s.userId = :userId" +
            " AND (:graduateId IS NULL OR s.graduateEntity.graduateId = :graduateId)" +
            " ORDER BY s.graduateEntity.id DESC")
    List<GraduateScrapEntity> findByUserIdOrderByGraduateEntityIdDesc(
            @Param("userId") String userId,
            @Param("graduateId") String graduateId,
            Pageable pageable);

}
