package com.example.demo.service;

import com.example.demo.entity.LectureTime;
import com.example.demo.entity.Room;
import com.example.demo.repository.LectureTimeRepository;
import com.example.demo.repository.RoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public LectureTime addLectureTime(String roomNumber, LocalTime startTime, LocalTime endTime, String lectureName, String dayOfWeek) {
        Room room = roomRepository.findById(roomNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의실이 존재하지 않습니다."));

        LectureTime lectureTime = new LectureTime();
        lectureTime.setStartTime(startTime);
        lectureTime.setEndTime(endTime);
        lectureTime.setLectureName(lectureName);
        lectureTime.setRoom(room);
        lectureTime.setDayOfWeek(DayOfWeek.valueOf(dayOfWeek));


        return lectureTimeRepository.save(lectureTime);
    }

    // 모든 강의실과 강의 시간 조회
    @Transactional
    public List<Map<String, Object>> getAllRoomsWithLectureTimes() {
        List<Room> rooms = roomRepository.findAll();

        // 모든 강의실과 해당 강의 시간을 매핑
        return rooms.stream().map(room -> {
            Map<String, Object> roomMap = Map.of(
                    "roomNumber", room.getRoomNumber(),
                    "lectureTimes", room.getLectureTimes().stream().map(lectureTime -> Map.of(
                            "id", lectureTime.getId(),
                            "lectureName", lectureTime.getLectureName(),
                            "startTime", lectureTime.getStartTime().toString(),
                            "endTime", lectureTime.getEndTime().toString(),
                            "dayOfWeek", lectureTime.getDayOfWeek().toString()
                    )).collect(Collectors.toList())
            );
            return roomMap;
        }).collect(Collectors.toList());
    }

    // 특정 강의 이름으로 강의 삭제
    public int deleteLecturesByName(String lectureName) {
        List<LectureTime> lectures = lectureTimeRepository.findByLectureName(lectureName);
        if (lectures.isEmpty()) {
            return 0; // 삭제할 강의가 없을 경우
        }
        lectureTimeRepository.deleteAll(lectures);
        return lectures.size(); // 삭제한 강의 개수를 반환
    }

    // 모든 강의 삭제
    public int deleteAllLectures() {
        List<LectureTime> allLectures = lectureTimeRepository.findAll(); // 모든 강의 조회
        int count = allLectures.size(); // 삭제 전 강의 수
        lectureTimeRepository.deleteAll(); // 모든 강의 삭제
        return count; // 삭제한 강의 수 반환
    }
}
