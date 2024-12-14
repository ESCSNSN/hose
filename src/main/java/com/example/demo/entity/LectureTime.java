package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
public class LectureTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime startTime;  // 강의 시작 시간 (예: 09:00)
    private LocalTime endTime;    // 강의 종료 시간 (예: 10:30)
    private String lectureName;   // 강의명 또는 수업명 (관리자가 추가할 때 입력 가능)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_number")
    private Room room;
}
