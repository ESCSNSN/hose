package com.example.demo.controller;

import com.example.demo.dto.CodingDTO;
import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.FreeDTO;
import com.example.demo.entity.CodingEntity;
import com.example.demo.exception.UnauthorizedDeletionException;
import com.example.demo.repository.CodingRepository;
import com.example.demo.service.CodingService;
import com.example.demo.service.CommentService;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class CodingController {

    private final CodingService codingService;
    private final CommentService commentService;
    private final PostReportService postReportService;
    private final CodingRepository codingRepository;

    // GET /api/board/coding
    @GetMapping("/coding")
    public Page<CodingDTO> paging(@RequestParam(value = "page", required = false) Integer page,
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "coding_created_time"));
        Page<CodingDTO> codingList;

        if ((searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty()) &&
                (typeKeyword == null || typeKeyword.isEmpty())) {
            codingList = codingService.paging(pageable);
        } else {
            codingList = codingService.searchByTitleOrContentOrHashtagOrType(searchKeyword, contentKeyword, hashtagKeyword, typeKeyword, pageable);
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
    public ResponseEntity<CodingDTO> save(@ModelAttribute CodingDTO codingDTO) throws IOException {
        codingService.save(codingDTO);
        return ResponseEntity.ok(codingDTO); // 200 OK
    }

    // GET /api/board/coding/{id}
    @GetMapping("/coding/{id}")
    public CodingDTO findById(@PathVariable Long id) {
        return codingService.findByID(id);
    }

    // GET /api/board/notice/update/{id} (업데이트 폼 요청)
    @GetMapping("/coding/update/{id}")
    public ResponseEntity<CodingDTO> updateForm(
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") String userId) {
        CodingDTO codingDTO = codingService.findByID(id, userId);
        return ResponseEntity.ok(codingDTO);
    }

    // POST /api/board/coding/update
    @PostMapping("/coding/update")
    public CodingDTO update(@RequestBody CodingDTO codingDTO) {
        return codingService.update(codingDTO); // 업데이트된 CodingDTO 반환
    }

    // DELETE /api/board/quest/delete/{id}
    @DeleteMapping("/coding/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") String userId) {
        boolean isDeleted = codingService.delete(id, userId);
        if (isDeleted) {
            commentService.deleteCommentsByTarget("Coding", id);
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "사용자 권한이 없습니다.");
        }
    }

    // POST /api/board/free/{id}/like
    @PostMapping("/coding/{id}/like")
    public ResponseEntity<Void> likeQuest(
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") String userId) {
        codingService.increaseLike(id);
        return ResponseEntity.ok().build(); // 200 OK
    }

    // POST /api/board/free/{id}/scrap
    @PostMapping("/coding/{id}/scrap")
    public ResponseEntity<Void> scrapQuest(
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") String userId) {
        codingService.toggleScrap(id);
        return ResponseEntity.ok().build(); // 200 OK
    }

    @GetMapping("/coding/top-liked")
    public ResponseEntity<List<CodingDTO>> getTopLikedCodings() {
        List<CodingDTO> topLikedCodings = codingService.getTopLikedCodings();
        if (topLikedCodings.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(topLikedCodings); // 200 OK
    }


    @GetMapping("/coding/sort-by-likes")
    public Page<CodingDTO> sortByLikes(
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "coding_like"));

        // 검색 파라미터가 없으면 단순히 좋아요 순 정렬
        if ((searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty()) &&
                (typeKeyword == null || typeKeyword.isEmpty())) {
            return codingService.sortByLikes(pageable);
        } else {
            // 검색 파라미터가 있으면 검색과 함께 좋아요 순 정렬
            return codingService.searchAndSortByLikes(searchKeyword, contentKeyword, hashtagKeyword, typeKeyword, pageable);
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
        commentService.addComment(commentDTO);
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
