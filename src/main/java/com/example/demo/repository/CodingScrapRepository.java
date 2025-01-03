package com.example.demo.repository;

import com.example.demo.entity.CodingScrapEntity;
import com.example.demo.entity.FreeScrapEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CodingScrapRepository extends JpaRepository<CodingScrapEntity,Long> {
    boolean existsByUserIdAndCodingEntityId(String userId, Long codingEntityId);
    Optional<CodingScrapEntity> findByUserIdAndCodingEntityId(String userId, Long codingEntityId);
    List<CodingScrapEntity> findByUserId(String userId);

    // 커서 기반 페이징을 위한 메서드
    @Query("SELECT s FROM CodingScrapEntity s WHERE s.userId = :userId AND s.codingEntity.id < :lastId ORDER BY s.codingEntity.id DESC")
    List<CodingScrapEntity> findTopByUserIdAndIdLessThan(@Param("userId") String userId, @Param("lastId") Long lastId, Pageable pageable);

}
