package com.example.demo.repository;


import com.example.demo.entity.FreeFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreeFileRepository extends JpaRepository<FreeFileEntity,Long> {
}
