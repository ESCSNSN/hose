package com.example.demo.repository;

import com.example.demo.entity.QuestScrapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuestScrapRepository extends JpaRepository<QuestScrapEntity, Long> {
    boolean existsByUserIdAndQuestEntityId(String userId, Long questEntityId);
    Optional<QuestScrapEntity> findByUserIdAndQuestEntityId(String userId, Long questEntityId);
    List<QuestScrapEntity> findByUserId(String userId);

    // 커서 기반 페이징을 위한 메서드
    @Query("SELECT s FROM QuestScrapEntity s WHERE s.userId = :userId AND s.questEntity.id < :lastId ORDER BY s.questEntity.id DESC")
    List<QuestScrapEntity> findTopByUserIdAndIdLessThan(@Param("userId") String userId, @Param("lastId") Long lastId, org.springframework.data.domain.Pageable pageable);


}