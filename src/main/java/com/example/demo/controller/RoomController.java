package com.example.demo.controller;

import com.example.demo.entity.LectureTime;
import com.example.demo.entity.Room;
import com.example.demo.service.RoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // (프론트 요청용) 모든 강의실 강의 시간 조회
    @GetMapping("api/rooms/all")
    public List<Map<String, Object>> getAllRoomsWithLectureTimes() {
        return roomService.getAllRoomsWithLectureTimes();
    }


}
