package com.example.demo.repository;

import com.example.demo.entity.ApplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplyRepository extends JpaRepository<ApplyEntity, Long> {
    Optional<ApplyEntity> findByIdAndStudyEntityId(Long id, Long studyId);

    boolean existsByApplyUserIdAndStudyEntityId(String applyUserId, Long studyId);

    List<ApplyEntity> findByStudyEntityId(Long studyId);
}
