package com.example.demo.repository;

import com.example.demo.entity.QuestLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestLikeRepository extends JpaRepository<QuestLikeEntity, Long> {
    boolean existsByUserIdAndQuestEntityId(String userId, Long questEntityId);
    Optional<QuestLikeEntity> findByUserIdAndQuestEntityId(String userId, Long questEntityId);
}