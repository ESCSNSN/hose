package com.example.demo.controller;

import com.example.demo.dto.CodingDTO;
import com.example.demo.dto.CommentDTO;
import com.example.demo.exception.UnauthorizedDeletionException;
import com.example.demo.service.CodingService;
import com.example.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class CodingController {

    private final CodingService codingService;
    private final CommentService commentService;

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
    @PostMapping("/coding/save")
    public CodingDTO save(@RequestBody CodingDTO codingDTO) throws IOException {
        codingService.save(codingDTO);
        return codingDTO; // 저장된 CodingDTO 반환
    }

    // GET /api/board/coding/{id}
    @GetMapping("/coding/{id}")
    public CodingDTO findById(@PathVariable Long id) {
        return codingService.findByID(id);
    }

    // GET /api/board/coding/update/{id}
    @GetMapping("/coding/update/{id}")
    public CodingDTO updateForm(@PathVariable Long id) {
        return codingService.findByID(id);
    }

    // POST /api/board/coding/update
    @PostMapping("/coding/update")
    public CodingDTO update(@RequestBody CodingDTO codingDTO) {
        return codingService.update(codingDTO); // 업데이트된 CodingDTO 반환
    }

    // DELETE /api/board/coding/delete/{id}
    @DeleteMapping("/coding/delete/{id}")
    public void delete(@PathVariable Long id) {
        codingService.delete(id);
    }

    // POST /api/board/coding/{id}/like
    @PostMapping("/coding/{id}/like")
    public void likeCoding(@PathVariable Long id) {
        codingService.increaseLike(id); // toggleLike에서 increaseLike로 변경
    }


    // POST /api/board/coding/{id}/scrap
    @PostMapping("/coding/{id}/scrap")
    public void scrapCoding(@PathVariable Long id) {
        codingService.toggleScrap(id);
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
                                                 @RequestParam String userId) {
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
                                                @RequestParam String userId) {
        try {
            commentService.deleteComment(commentId, userId);
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
        } catch (UnauthorizedDeletionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // POST /api/board/coding/{id}/comments/{commentId}/report
    @PostMapping("/coding/{id}/comments/{commentId}/report")
    public ResponseEntity<String> reportComment(@PathVariable Long id,
                                                @PathVariable Long commentId,
                                                @RequestParam String reason,
                                                @RequestParam String reporterId) {
        commentService.reportComment(commentId, reporterId, reason);
        return ResponseEntity.ok("댓글이 성공적으로 신고되었습니다.");
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