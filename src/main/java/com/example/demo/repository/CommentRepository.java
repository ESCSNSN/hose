package com.example.demo.repository;

import com.example.demo.entity.CommentEntity;
import com.example.demo.entity.StudyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findByTargetTypeAndTargetId(String targetType, Long targetId, Pageable pageable);
    void deleteByTargetTypeAndTargetId(String targetType, Long targetId);
    List<StudyEntity> findByUserId(String userId);
}
