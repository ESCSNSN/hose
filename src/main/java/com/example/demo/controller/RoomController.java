package com.example.demo.controller;

import com.example.demo.entity.LectureTime;
import com.example.demo.entity.Room;
import com.example.demo.service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomController {

    private final RoomService roomService;
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }


    // (프론트 요청용) 특정 강의실 강의 시간 조회
    @GetMapping("/{roomNumber}/lectures")
    public List<Map<String, Object>> getLectureTimes(@PathVariable String roomNumber) {
        List<LectureTime> times = roomService.getLectureTimesForRoom(roomNumber);

        // 필요한 정보만 전달할 수 있도록 Map으로 변환
        return times.stream().map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", t.getId());
            map.put("lectureName", t.getLectureName());
            map.put("startTime", t.getStartTime().toString());
            map.put("endTime", t.getEndTime().toString());
            return map;
        }).toList();
    }
}
