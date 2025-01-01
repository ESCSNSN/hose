package com.example.demo.repository;

import com.example.demo.entity.CompetitionLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetitionLikeRepository extends JpaRepository<CompetitionLikeEntity, Long> {

    boolean existsByUserIdAndCompetitionEntityId(String userId, Long competitionEntityId);
    Optional<CompetitionLikeEntity> findByUserIdAndCompetitionEntityId(String userId, Long competitionEntityId);
}
