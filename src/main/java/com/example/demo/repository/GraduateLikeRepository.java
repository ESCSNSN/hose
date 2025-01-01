package com.example.demo.repository;

import com.example.demo.entity.GraduateLikeEntity;
import com.example.demo.entity.QuestLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GraduateLikeRepository extends JpaRepository<GraduateLikeEntity,Long> {
    boolean existsByUserIdAndGraduateEntityId(String userId, Long graduateEntityId);
    Optional<GraduateLikeEntity> findByUserIdAndGraduateEntityId(String userId, Long graduateEntityId);
}
