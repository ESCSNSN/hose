package com.example.demo.repository;

import com.example.demo.entity.FreeScrapEntity;
import com.example.demo.entity.QuestScrapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FreeScrapRepository extends JpaRepository<FreeScrapEntity,Long> {
    boolean existsByUserIdAndFreeEntityId(String userId, Long freeEntityId);
    Optional<FreeScrapEntity> findByUserIdAndFreeEntityId(String userId, Long freeEntityId);
    List<FreeScrapEntity> findByUserId(String userId);

    // 커서 기반 페이징을 위한 메서드
    @Query("SELECT s FROM FreeScrapEntity s WHERE s.userId = :userId AND s.id < :lastId ORDER BY s.id DESC")
    List<FreeScrapEntity> findTopByUserIdAndIdLessThan(@Param("userId") String userId, @Param("lastId") Long lastId, org.springframework.data.domain.Pageable pageable);


}
