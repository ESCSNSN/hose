package com.example.demo.repository;

import com.example.demo.entity.FreeLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FreeLikeRepository extends JpaRepository<FreeLikeEntity, Long> {
    boolean existsByUserIdAndFreeEntityId(String userId, Long freeEntityId);
    Optional<FreeLikeEntity> findByUserIdAndFreeEntityId(String userId, Long freeEntityId);
}
