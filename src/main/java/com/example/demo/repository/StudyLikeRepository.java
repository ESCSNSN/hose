package com.example.demo.repository;

import com.example.demo.entity.StudyLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyLikeRepository extends JpaRepository< StudyLikeEntity, Long> {
    boolean existsByUserIdAndStudyEntityId(String userId, Long studyEntityId);
    Optional<StudyLikeEntity> findByUserIdAndStudyEntityId(String userId, Long studyEntityId);
}
