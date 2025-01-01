package com.example.demo.repository;

import com.example.demo.entity.CodingLikeEntity;
import com.example.demo.entity.CompetitionLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodingLikeRepository extends JpaRepository<CodingLikeEntity, Long> {

    boolean existsByUserIdAndCodingEntityId(String userId, Long codingEntityId);
    Optional<CodingLikeEntity> findByUserIdAndCodingEntityId(String userId, Long codingEntityId);
}
