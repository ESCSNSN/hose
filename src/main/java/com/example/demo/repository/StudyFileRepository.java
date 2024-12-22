package com.example.demo.repository;

import com.example.demo.entity.StudyFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyFileRepository extends JpaRepository<StudyFileEntity, Long> {
}
