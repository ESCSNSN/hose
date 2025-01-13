package com.example.demo.controller;

import com.example.demo.dto.CodingDTO;
import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.FreeDTO;
import com.example.demo.entity.CodingEntity;
import com.example.demo.exception.UnauthorizedDeletionException;
import com.example.demo.repository.CodingRepository;
import com.example.demo.service.CodingService;
import com.example.demo.service.CommentService;
import com.example.demo.service.NotificationService;
import com.example.demo.service.PostReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class CodingController {

    private final CodingService codingService;
    private final CommentService commentService;
    private final NotificationService notificationService;
    private final PostReportService postReportService;
    private final CodingRepository codingRepository;

    // GET /api/board/coding
    @GetMapping("/coding")
    public Page<CodingDTO> paging(
            HttpServletRequest request,
                                  @RequestParam(value = "page", required = false) Integer page,
                                  @RequestParam(value = "size", defaultValue = "10") Integer size,
                                  @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                  @RequestParam(value = "contentKeyword", required = false) String contentKeyword,
                                  @RequestParam(value = "hashtagKeyword", required = false) String hashtagKeyword,
                                  @RequestParam(value = "typeKeyword", required = false) String typeKeyword) {

        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 10;
        }
        String userId = (String) request.getAttribute("username");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "coding_created_time"));
        Page<CodingDTO> codingList;

        if ((searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty()) &&
                (typeKeyword == null || typeKeyword.isEmpty())) {
            codingList = codingService.paging(userId,pageable);
        } else {
            codingList = codingService.searchByTitleOrContentOrHashtagOrType(userId,searchKeyword, contentKeyword, hashtagKeyword, typeKeyword, pageable);
        }

        return codingList;
    }

    // GET /api/board/coding/save
    @GetMapping("/coding/save")
    public CodingDTO saveForm() {
        return new CodingDTO(); // 기본 구조의 CodingDTO 반환
    }

    // POST /api/board/coding/save
    @PostMapping(value = "/coding/save", consumes = {"multipart/form-data"})
    public ResponseEntity<CodingDTO> save(@ModelAttribute CodingDTO codingDTO, HttpServletRequest request) throws IOException {
        String userId = (String) request.getAttribute("username");
        codingDTO.setUserID(userId);
        codingService.save(codingDTO);
        return ResponseEntity.ok(codingDTO); // 200 OK
    }

    // GET /api/board/quest/{id}
    @GetMapping("/coding/{id}")
    public ResponseEntity<CodingDTO> findById(@PathVariable Long id) {
        CodingDTO dto = codingService.findByID(id);
        return ResponseEntity.ok(dto);
    }

    // GET /api/board/notice/update/{id} (업데이트 폼 요청)
    @GetMapping("/coding/update/{id}")
    public ResponseEntity<CodingDTO> updateForm(
            @PathVariable Long id,
            HttpServletRequest request) {

        String userId = (String) request.getAttribute("username");
        CodingDTO codingDTO = codingService.findByID(id, userId);
        return ResponseEntity.ok(codingDTO);
    }

    // POST /api/board/coding/update
    @PostMapping(value = "/coding/update",consumes = {"multipart/form-data"})
    public CodingDTO update(HttpServletRequest request,@ModelAttribute CodingDTO codingDTO) throws IOException {
        String userId = (String) request.getAttribute("username");
        codingDTO.setUserID(userId);
        return codingService.update(codingDTO); // 업데이트된 CodingDTO 반환
    }

    // DELETE /api/board/quest/delete/{id}
    @DeleteMapping("/coding/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        boolean isDeleted = codingService.delete(id, userId);
        if (isDeleted) {
            commentService.deleteCommentsByTarget("Coding", id);
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "사용자 권한이 없습니다.");
        }
    }

    // POST /api/board/quest/{id}/like
    @PostMapping("/coding/{id}/like")
    public ResponseEntity<String> toggleLikeQuest(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");

        try {
            codingService.toggleLike(id, userId);
            return ResponseEntity.ok("Like toggled successfully");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 새로운 좋아요 상태 확인 엔드포인트
    @GetMapping("/coding/{id}/like-status")
    public ResponseEntity<Map<String, Object>> checkLikeStatus(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username"); // 또는 다른 방식으로 사용자 ID 가져오기

        boolean isLiked = codingService.hasUserLikedCoding(id, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", isLiked);
        return ResponseEntity.ok(response);
    }

    // 새로운 스크랩 토글 엔드포인트
    @PostMapping("/coding/{id}/scrap")
    public ResponseEntity<Map<String, Object>> toggleScrapQuest(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");

        try {
            boolean isScrapped = codingService.toggleScrap(id, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Scrap toggled successfully");
            response.put("scrapped", isScrapped);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 새로운 좋아요 상태 확인 엔드포인트
    @GetMapping("/coding/{id}/scrap-status")
    public ResponseEntity<Map<String, Object>> checkScrapStatus(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");

        boolean isScraped = codingService.hasUserScrappedCoding(id, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("scrap", isScraped);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/coding/top-liked")
    public ResponseEntity<List<CodingDTO>> getTopLikedCodings(HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        List<CodingDTO> topLikedCodings = codingService.getTopLikedCodings(userId);
        if (topLikedCodings.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(topLikedCodings); // 200 OK
    }


    @GetMapping("/coding/sort-by-likes")
    public Page<CodingDTO> sortByLikes(
            HttpServletRequest request,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "contentKeyword", required = false) String contentKeyword,
            @RequestParam(value = "hashtagKeyword", required = false) String hashtagKeyword,
            @RequestParam(value = "typeKeyword", required = false) String typeKeyword
    ) {

        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 10;
        }
        String userId = (String) request.getAttribute("username");

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "codingLike"));

        // 검색 파라미터가 없으면 단순히 좋아요 순 정렬
        if ((searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty()) &&
                (typeKeyword == null || typeKeyword.isEmpty())) {
            return codingService.sortByLikes(userId,pageable);
        } else {
            // 검색 파라미터가 있으면 검색과 함께 좋아요 순 정렬
            return codingService.searchAndSortByLikes(userId,searchKeyword, contentKeyword, hashtagKeyword, typeKeyword, pageable);
        }
    }
    // POST /api/board/coding/{id}/comments/add
    @PostMapping("/coding/{id}/comments/add")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long id,
                                                 @RequestParam(required = false) Long parentCommentId,
                                                 @RequestParam String content,
                                                 HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(content);
        commentDTO.setUserId(userId);
        commentDTO.setTargetType("Coding");
        commentDTO.setTargetId(id);
        commentDTO.setParentCommentId(parentCommentId);
        String postUser = codingService.findByID(id).getUserID();
        commentService.addComment(commentDTO, postUser);
        return ResponseEntity.ok(commentDTO);
    }

    // POST /api/board/coding/{id}/comments/{commentId}/delete
    @PostMapping("/coding/{id}/comments/{commentId}/delete")
    public ResponseEntity<String> deleteComment(@PathVariable Long id,
                                                @PathVariable Long commentId,
                                                HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        try {
            commentService.deleteComment(commentId, userId);
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
        } catch (UnauthorizedDeletionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // GET /api/board/coding/{id}/comments
    @GetMapping("/coding/{id}/comments")
    public ResponseEntity<Page<CommentDTO>> getComments(@PathVariable Long id,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CommentDTO> comments = commentService.getComments("Coding", id, pageable);
        return ResponseEntity.ok(comments);
    }

}
