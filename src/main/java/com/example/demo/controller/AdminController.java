package com.example.demo.controller;

import com.example.demo.dto.CommentReportDTO;
import com.example.demo.dto.PostReportDTO;
import com.example.demo.entity.LectureTime;
import com.example.demo.entity.Room;
import com.example.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
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
    private final CodingService codingService;
    private final CommentService commentService;
    private final CompetitionService competitionService;

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
     * 예: /api/admin/rooms/102/lectures/add?startTime=09:00&endTime=10:00&lectureName=자료구조&dayOfWeek=MONDAY
     */
    @PostMapping("/rooms/{roomNumber}/lectures/add")
    public ResponseEntity<Map<String, Object>> addLectureTime(@PathVariable String roomNumber,
                                                              @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
                                                              @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
                                                              @RequestParam String lectureName,
                                                              @RequestParam DayOfWeek dayOfWeek)
            {
        LectureTime lectureTime = roomService.addLectureTime(roomNumber, startTime, endTime, lectureName, String.valueOf(dayOfWeek));

        Map<String, Object> response = new HashMap<>();
        response.put("id", lectureTime.getId());
        response.put("roomNumber", roomNumber);
        response.put("lectureName", lectureTime.getLectureName());
        response.put("startTime", lectureTime.getStartTime().toString());
        response.put("endTime", lectureTime.getEndTime().toString());
        response.put("dayOfWeek", lectureTime.getDayOfWeek().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 강의 이름으로 강의 삭제
     * URL: DELETE /api/admin/lectures/delete-by-name
     * 예: /api/admin/lectures/delete-by-name?lectureName=자료구조
     */
    @DeleteMapping("/lectures/delete-by-name")
    public ResponseEntity<Map<String, Object>> deleteLecturesByName(@RequestParam String lectureName) {
        try {
            int deletedCount = roomService.deleteLecturesByName(lectureName);
            if (deletedCount == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("message", "No lectures found with the name '" + lectureName + "'."));
            }
            return ResponseEntity.ok(Map.of(
                    "message", "Lectures with name '" + lectureName + "' have been deleted successfully.",
                    "deletedCount", deletedCount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "Internal server error."));
        }
    }

    /**
     * 모든 강의실 모든 강의 삭제
     * URL: DELETE /api/admin/lectures/delete-all
     * 예: /api/admin/lectures/delete-all
     */

    @DeleteMapping("/lectures/delete-all")
    public ResponseEntity<Map<String, String>> deleteAllLectures() {
        try {
            int deletedCount = roomService.deleteAllLectures();
            return ResponseEntity.ok(Map.of(
                    "message", "All lectures have been deleted successfully.",
                    "deletedCount", String.valueOf(deletedCount)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("message", "Internal server error."));
        }
    }



    /**
     * 관리자용 코딩 게시물 삭제
     * 예: DELETE /api/admin/coding/{id}
     * 해당 게시물 및 관련 댓글들을 관리자 권한으로 삭제
     */
    @DeleteMapping("/coding/{id}")
    public ResponseEntity<String> deleteCodingPostByAdmin(@PathVariable Long id) {
        try {
            // 게시물 존재 여부 검증 및 삭제 로직 (관리자용)
            codingService.deleteByAdmin(id);
            return ResponseEntity.ok("코딩 게시물이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");
        }
    }

    /**
     * 관리자용 코딩 게시물의 특정 댓글 삭제
     * 예: DELETE /api/admin/coding/{id}/comments/{commentId}
     * userId 없이도 관리자 권한으로 해당 댓글 삭제
     */
    @DeleteMapping("/coding/{id}/comments/{commentId}")
    public ResponseEntity<String> deleteCodingCommentByAdmin(@PathVariable Long id,
                                                             @PathVariable Long commentId) {
        try {
            // 관리자용 댓글 삭제 로직
            commentService.deleteCommentByAdmin(commentId);
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글을 찾을 수 없습니다.");
        }
    }

    /**
     * 관리자용 대회 게시물 삭제
     * 예: DELETE /api/admin/competition/{id}
     */
    @DeleteMapping("/competition/{id}")
    public ResponseEntity<String> deleteCompetitionPostByAdmin(@PathVariable Long id) {
        try {
            competitionService.deleteByAdmin(id);
            return ResponseEntity.ok("대회 게시물이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물을 찾을 수 없습니다.");
        }
    }

    /**
     * 관리자용 대회 게시물 특정 댓글 삭제
     * 예: DELETE /api/admin/competition/{id}/comments/{commentId}
     */
    @DeleteMapping("/competition/{id}/comments/{commentId}")
    public ResponseEntity<String> deleteCompetitionCommentByAdmin(@PathVariable Long id,
                                                                  @PathVariable Long commentId) {
        try {
            commentService.deleteCommentByAdmin(commentId);
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글을 찾을 수 없습니다.");
        }
    }



}
