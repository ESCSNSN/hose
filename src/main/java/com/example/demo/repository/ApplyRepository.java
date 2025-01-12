package com.example.demo.repository;

import com.example.demo.entity.ApplyEntity;
import com.example.demo.entity.StudyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplyRepository extends JpaRepository<ApplyEntity, Long> {
    Optional<ApplyEntity> findByIdAndStudyEntityId(Long id, Long studyId);

    boolean existsByApplyUserIdAndStudyEntityId(String applyUserId, Long studyId);

    List<ApplyEntity> findByStudyEntityId(Long studyId);

    @Query("SELECT a.studyEntity FROM ApplyEntity a " +
            "WHERE a.applyUserId = :userId " +
            "AND (:studyid IS NULL OR :studyid = '' OR a.studyEntity.studyId LIKE CONCAT('%', :studyid, '%')) " +
            "AND a.studyEntity.deadline > CURRENT_TIMESTAMP " +
            "ORDER BY a.studyEntity.deadline ASC")
    Page<StudyEntity> findAppliedStudiesByUserId(
            @Param("userId") String userId,
            @Param("studyid") String studyid,
            Pageable pageable);
}
