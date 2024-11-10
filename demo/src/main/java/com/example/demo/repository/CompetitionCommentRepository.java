// src/main/java/com/example/demo/repository/CompetitionCommentRepository.java
package com.example.demo.repository;

import com.example.demo.entity.CompetitionCommentEntity;
import com.example.demo.entity.CompetitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetitionCommentRepository extends JpaRepository<CompetitionCommentEntity, Long> {
    List<CompetitionCommentEntity> findByCompetitionEntity(CompetitionEntity competitionEntity);
}
