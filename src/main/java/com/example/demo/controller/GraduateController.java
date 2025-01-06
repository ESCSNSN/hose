package com.example.demo.controller;



import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.FreeDTO;
import com.example.demo.dto.GraduateDTO;
import com.example.demo.exception.UnauthorizedDeletionException;
import com.example.demo.service.FreeService;
import com.example.demo.service.GraduateService;
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
import com.example.demo.service.CommentService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class GraduateController {

    private final GraduateService graduateService;
    private final CommentService commentService;


    // GET /api/board/free
    @GetMapping("/graduate")
    public Page<GraduateDTO> paging(@RequestParam(value = "page", required = false) Integer page,
                                @RequestParam(value = "size", defaultValue = "10") Integer size,
                                @RequestParam(value = "graduateId", required = false) String graduateId,
                                @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                @RequestParam(value = "contentKeyword", required = false) String contentKeyword,
                                @RequestParam(value = "hashtagKeyword", required = false) String hashtagKeyword) {

        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "graduate_created_time"));
        Page<GraduateDTO> graduateList;

        if (
                (graduateId == null || graduateId.isEmpty()) &&
                (searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty())) {
            graduateList = graduateService.paging(pageable);
        } else {
            graduateList = graduateService.searchByTitleOrContentOrHashtagOrType(graduateId,searchKeyword, contentKeyword, hashtagKeyword, pageable);
        }

        return graduateList;
    }

    // GET /api/board/free/save
    @GetMapping("/graduate/save")
    public GraduateDTO saveForm() {
        return new GraduateDTO(); // 기본 구조의 CodingDTO 반환
    }

    // POST /api/board/free/save
    @PostMapping(value = "/graduate/save")
    public ResponseEntity<GraduateDTO> save(@RequestBody GraduateDTO graduateDTO, HttpServletRequest request) throws IOException {
        String userId = "202001685";
        graduateDTO.setUserID(userId); // Setter 메서드 이름 수정
        graduateService.save(graduateDTO);
        return ResponseEntity.ok(graduateDTO); // 200 OK
    }



    // GET /api/board/free/{id}
    @GetMapping("/graduate/{id}")
    public GraduateDTO findById(@PathVariable Long id) {
        return graduateService.findByID(id);
    }

    // GET /api/board/free/update/{id} (업데이트 폼 요청)
    @GetMapping("/graduate/update/{id}")
    public ResponseEntity<GraduateDTO> updateForm(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = "202001685";
        GraduateDTO graduateDTO = graduateService.findByID(id, userId);
        return ResponseEntity.ok(graduateDTO);
    }

    // POST /api/board/coding/update
    @PostMapping("/graduate/update")
    public GraduateDTO update(@RequestBody GraduateDTO graduateDTO) {
        return graduateService.update(graduateDTO); // 업데이트된 CodingDTO 반환
    }

    // DELETE /api/board/free/delete/{id}
    @DeleteMapping("/graduate/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        boolean isDeleted = graduateService.delete(id, userId);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "사용자 권한이 없습니다.");
        }
    }

    // POST /api/board/quest/{id}/like
    @PostMapping("/graduate/{id}/like")
    public ResponseEntity<String> toggleLikeQuest(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");

        try {
            graduateService.toggleLike(id, userId);
            return ResponseEntity.ok("Like toggled successfully");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 새로운 좋아요 상태 확인 엔드포인트
    @GetMapping("/graduate/{id}/like-status")
    public ResponseEntity<Map<String, Object>> checkLikeStatus(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username"); // 또는 다른 방식으로 사용자 ID 가져오기

        boolean isLiked = graduateService.hasUserLikedGraduate(id, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", isLiked);
        return ResponseEntity.ok(response);
    }

    // 새로운 스크랩 토글 엔드포인트
    @PostMapping("/graduate/{id}/scrap")
    public ResponseEntity<Map<String, Object>> toggleScrapQuest(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");

        try {
            boolean isScrapped = graduateService.toggleScrap(id, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Scrap toggled successfully");
            response.put("scrapped", isScrapped);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 새로운 좋아요 상태 확인 엔드포인트
    @GetMapping("/graduate/{id}/scrap-status")
    public ResponseEntity<Map<String, Object>> checkScrapStatus(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");

        boolean isScraped = graduateService.hasUserScrappedGraduate(id, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("scrap", isScraped);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/graduate/top-liked")
    public ResponseEntity<List<GraduateDTO>> getTopLikedFrees() {
        List<GraduateDTO> topLikedFrees = graduateService.getTopLikedFrees();
        if (topLikedFrees.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(topLikedFrees); // 200 OK
    }


    @GetMapping("/graduate/sort-by-likes")
    public Page<GraduateDTO> sortByLikes(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "graduateId", required = false) String graduateId,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "contentKeyword", required = false) String contentKeyword,
            @RequestParam(value = "hashtagKeyword", required = false) String hashtagKeyword

    ) {

        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "graduate_like"));
        Page<GraduateDTO> graduateList;

        // 검색 파라미터가 없으면 단순히 좋아요 순 정렬
        if (
                (graduateId == null || graduateId.isEmpty()) &&
                (searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty())
        ) {
            graduateList =  graduateService.sortByLikes(pageable);
            return graduateList;
        } else {
            // 검색 파라미터가 있으면 검색과 함께 좋아요 순 정렬
            return graduateService.searchAndSortByLikes(graduateId,searchKeyword, contentKeyword, hashtagKeyword, pageable);
        }
    }

    // POST /api/board/free/{id}/comments/add
    @PostMapping("/graduate/{id}/comments/add")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long id,
                                                 @RequestParam(required = false) Long parentCommentId,
                                                 @RequestParam String content,
                                                 @RequestParam Long anonymousId,
                                                 HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(content);
        commentDTO.setUserId(userId);
        commentDTO.setTargetType("free");
        commentDTO.setTargetId(id);
        commentDTO.setParentCommentId(parentCommentId);
        commentDTO.setAnonymousId(anonymousId);
        commentService.addComment(commentDTO);
        return ResponseEntity.ok(commentDTO);
    }

    // POST /api/board/free/{id}/comments/{commentId}/delete
    @PostMapping("/graduate/{id}/comments/{commentId}/delete")
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


    // GET /api/board/free/{id}/comments
    @GetMapping("/graduate/{id}/comments")
    public ResponseEntity<Page<CommentDTO>> getComments(@PathVariable Long id,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CommentDTO> comments = commentService.getComments("graduate", id, pageable);
        return ResponseEntity.ok(comments);
    }

}
