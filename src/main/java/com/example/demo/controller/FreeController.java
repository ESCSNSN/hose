package com.example.demo.controller;


import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.FreeDTO;
import com.example.demo.exception.UnauthorizedDeletionException;
import com.example.demo.service.FreeService;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class FreeController {

    private final FreeService freeService;
    private final CommentService commentService;

    // GET /api/board/coding
    @GetMapping("/free")
    public Page<FreeDTO> paging(@RequestParam(value = "page", required = false) Integer page,
                                @RequestParam(value = "size", defaultValue = "10") Integer size,
                                @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                @RequestParam(value = "contentKeyword", required = false) String contentKeyword,
                                @RequestParam(value = "hashtagKeyword", required = false) String hashtagKeyword) {

        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "free_created_time"));
        Page<FreeDTO> freeList;

        if ((searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty())) {
            freeList = freeService.paging(pageable);
        } else {
            freeList = freeService.searchByTitleOrContentOrHashtagOrType(searchKeyword, contentKeyword, hashtagKeyword, pageable);
        }

        return freeList;
    }

    // GET /api/board/coding/save
    @GetMapping("/free/save")
    public FreeDTO saveForm() {
        return new FreeDTO(); // 기본 구조의 CodingDTO 반환
    }

    // POST /api/board/coding/save
    @PostMapping(value = "/free/save", consumes = {"multipart/form-data"})
    public ResponseEntity<FreeDTO> save(@ModelAttribute FreeDTO freeDTO) throws IOException {
        freeService.save(freeDTO);
        return ResponseEntity.ok(freeDTO); // 200 OK
    }


    // GET /api/board/coding/{id}
    @GetMapping("/free/{id}")
    public FreeDTO findById(@PathVariable Long id) {
        return freeService.findByID(id);
    }

    // GET /api/board/notice/update/{id} (업데이트 폼 요청)
    @GetMapping("/free/update/{id}")
    public ResponseEntity<FreeDTO> updateForm(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        FreeDTO freeDTO = freeService.findByID(id, userId);
        return ResponseEntity.ok(freeDTO);
    }

    // POST /api/board/coding/update
    @PostMapping("/free/update")
    public FreeDTO update(@RequestBody FreeDTO freeDTO) {
        return freeService.update(freeDTO); // 업데이트된 CodingDTO 반환
    }

    // DELETE /api/board/free/delete/{id}
    @DeleteMapping("/free/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        boolean isDeleted = freeService.delete(id, userId);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "사용자 권한이 없습니다.");
        }
    }

    // POST /api/board/free/{id}/like
    @PostMapping("/free/{id}/like")
    public ResponseEntity<Void> likeQuest(
            @PathVariable Long id,
            HttpServletRequest request) {
        freeService.increaseLike(id);
        return ResponseEntity.ok().build(); // 200 OK
    }

    // POST /api/board/free/{id}/scrap
    @PostMapping("/free/{id}/scrap")
    public ResponseEntity<Void> scrapQuest(
            @PathVariable Long id,
            HttpServletRequest request) {
        freeService.toggleScrap(id);
        return ResponseEntity.ok().build(); // 200 OK
    }

    @GetMapping("/free/top-liked")
    public ResponseEntity<List<FreeDTO>> getTopLikedFrees() {
        List<FreeDTO> topLikedFrees = freeService.getTopLikedFrees();
        if (topLikedFrees.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(topLikedFrees); // 200 OK
    }


    @GetMapping("/free/sort-by-likes")
    public Page<FreeDTO> sortByLikes(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "free_like"));
        Page<FreeDTO> freeList;

        // 검색 파라미터가 없으면 단순히 좋아요 순 정렬
        if ((searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty())
                ) {
            freeList =  freeService.sortByLikes(pageable);
            return freeList;
        } else {
            // 검색 파라미터가 있으면 검색과 함께 좋아요 순 정렬
            return freeService.searchAndSortByLikes(searchKeyword, contentKeyword, hashtagKeyword, pageable);
        }
    }

    // POST /api/board/free/{id}/comments/add
    @PostMapping("/free/{id}/comments/add")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long id,
                                                 @RequestParam(required = false) Long parentCommentId,
                                                 @RequestParam String content,
                                                 HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(content);
        commentDTO.setUserId(userId);
        commentDTO.setTargetType("free");
        commentDTO.setTargetId(id);
        commentDTO.setParentCommentId(parentCommentId);
        commentService.addComment(commentDTO);
        return ResponseEntity.ok(commentDTO);
    }

    // POST /api/board/free/{id}/comments/{commentId}/delete
    @PostMapping("/free/{id}/comments/{commentId}/delete")
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
    @GetMapping("/free/{id}/comments")
    public ResponseEntity<Page<CommentDTO>> getComments(@PathVariable Long id,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CommentDTO> comments = commentService.getComments("free", id, pageable);
        return ResponseEntity.ok(comments);
    }

}
