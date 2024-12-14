package com.example.demo.repository;

import com.example.demo.entity.LectureTime;
import com.example.demo.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureTimeRepository extends JpaRepository<LectureTime, Long> {
    List<LectureTime> findByRoom(Room room);
}
