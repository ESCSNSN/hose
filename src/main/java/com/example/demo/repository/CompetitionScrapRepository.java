package com.example.demo.repository;

import com.example.demo.entity.CompetitionScrapEntity;
import com.example.demo.entity.FreeScrapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CompetitionScrapRepository extends JpaRepository<CompetitionScrapEntity,Long> {
    boolean existsByUserIdAndCompetitionEntityId(String userId, Long competitionEntityId);
    Optional<CompetitionScrapEntity> findByUserIdAndCompetitionEntityId(String userId, Long competitionEntityId);
    List<CompetitionScrapEntity> findByUserId(String userId);

    // 커서 기반 페이징을 위한 메서드
    @Query("SELECT s FROM CompetitionScrapEntity s WHERE s.userId = :userId AND s.competitionEntity.id < :lastId ORDER BY s.competitionEntity.id DESC")
    List<CompetitionScrapEntity> findTopByUserIdAndIdLessThan(@Param("userId") String userId, @Param("lastId") Long lastId, org.springframework.data.domain.Pageable pageable);


}
