package com.example.demo.controller;

import com.example.demo.dto.CommentReportDTO;
import com.example.demo.dto.PostReportDTO;
import com.example.demo.entity.LectureTime;
import com.example.demo.entity.Room;
import com.example.demo.service.CommentReportService;
import com.example.demo.service.PostReportService;
import com.example.demo.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.time.LocalTime;
import java.util.HashMap;

/**
 * 관리자용 댓글 신고 관리 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final CommentReportService commentReportService;
    private final PostReportService postReportService;
    private final RoomService roomService;

    /**
     * 모든 댓글 신고 목록 조회
     * URL: GET /api/admin/comment-reports
     *
     * @return 모든 댓글 신고 DTO 리스트
     */
    @GetMapping("/comment-reports")
    public ResponseEntity<List<CommentReportDTO>> getAllCommentReports() {
        List<CommentReportDTO> reports = commentReportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    /**
     * 특정 댓글의 신고 목록 조회
     * URL: GET /api/admin/comment-reports/{commentId}
     *
     * @param commentId 댓글 ID
     * @return 특정 댓글에 대한 신고 DTO 리스트
     */
    @GetMapping("/comment-reports/{commentId}")
    public ResponseEntity<List<CommentReportDTO>> getCommentReportsByCommentId(@PathVariable Long commentId) {
        List<CommentReportDTO> reports = commentReportService.getReportsByCommentId(commentId);
        return ResponseEntity.ok(reports);
    }

    /**
     * 모든 게시물 신고 목록 조회
     * URL: GET /api/admin/post-reports
     *
     * @return 모든 게시물 신고 DTO 리스트
     */
    @GetMapping("/post-reports")
    public ResponseEntity<List<PostReportDTO>> getAllPostReports() {
        List<PostReportDTO> reports = postReportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    /**
     * 특정 게시물 신고 목록 조회
     * URL: GET /api/admin/post-reports/{postId}
     *
     * @param postId 게시물 ID
     * @return 특정 게시물 신고 DTO 리스트
     */

    @GetMapping("/post-reports/{postId}")
    public ResponseEntity<List<PostReportDTO>> getPostReportsByPostId(@PathVariable Long postId) {
        List<PostReportDTO> reports = postReportService.getReportsByPostId(postId);
        return ResponseEntity.ok(reports);
    }

    /**
     * 강의실 추가 (관리자 전용)
     * URL: POST /api/admin/rooms/add
     * 예: /api/admin/rooms/add?roomNumber=102
     */
    @PostMapping("/rooms/add")
    public ResponseEntity<Room> addRoom(@RequestParam String roomNumber) {
        Room room = roomService.addRoom(roomNumber);
        return ResponseEntity.ok(room);
    }

    /**
     * 특정 강의실에 강의 시간 추가 (관리자 전용)
     * URL: POST /api/admin/rooms/{roomNumber}/lectures/add
     * 예: /api/admin/rooms/102/lectures/add?startTime=09:00&endTime=10:30&lectureName=알고리즘
     */
    @PostMapping("/rooms/{roomNumber}/lectures/add")
    public ResponseEntity<Map<String, Object>> addLectureTime(@PathVariable String roomNumber,
                                                              @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
                                                              @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
                                                              @RequestParam String lectureName) {
        LectureTime lectureTime = roomService.addLectureTime(roomNumber, startTime, endTime, lectureName);

        Map<String, Object> response = new HashMap<>();
        response.put("id", lectureTime.getId());
        response.put("roomNumber", roomNumber);
        response.put("lectureName", lectureTime.getLectureName());
        response.put("startTime", lectureTime.getStartTime().toString());
        response.put("endTime", lectureTime.getEndTime().toString());

        return ResponseEntity.ok(response);
    }



}
