package com.example.demo.service;

import com.example.demo.entity.LectureTime;
import com.example.demo.entity.Room;
import com.example.demo.repository.LectureTimeRepository;
import com.example.demo.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final LectureTimeRepository lectureTimeRepository;

    public RoomService(RoomRepository roomRepository, LectureTimeRepository lectureTimeRepository) {
        this.roomRepository = roomRepository;
        this.lectureTimeRepository = lectureTimeRepository;
    }

    // 새로운 강의실 추가 (관리자 전용)
    public Room addRoom(String roomNumber) {
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        return roomRepository.save(room);
    }

    // 특정 강의실에 강의 시간 추가 (관리자 전용)
    public LectureTime addLectureTime(String roomNumber, LocalTime startTime, LocalTime endTime, String lectureName) {
        Room room = roomRepository.findById(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의실이 존재하지 않습니다."));

        LectureTime lectureTime = new LectureTime();
        lectureTime.setStartTime(startTime);
        lectureTime.setEndTime(endTime);
        lectureTime.setLectureName(lectureName);
        lectureTime.setRoom(room);

        return lectureTimeRepository.save(lectureTime);
    }

    // 특정 강의실의 강의 시간 조회 (프론트 요청용)
    public List<LectureTime> getLectureTimesForRoom(String roomNumber) {
        Room room = roomRepository.findById(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의실이 존재하지 않습니다."));
        return lectureTimeRepository.findByRoom(room);
    }
}
