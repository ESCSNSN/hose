package com.example.demo.controller;


import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.QuestDTO;
import com.example.demo.exception.UnauthorizedDeletionException;
import com.example.demo.service.QuestService;
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
public class QuestController {

    private final QuestService questService;
    private final CommentService commentService;

    // GET /api/board/quest
    @GetMapping("/quest")
    public Page<QuestDTO> paging(@RequestParam(value = "page", required = false) Integer page,
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "quest_created_time"));
        Page<QuestDTO> questList;

        if ((searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty())) {
            questList = questService.paging(pageable);
        } else {
            questList = questService.searchByTitleOrContentOrHashtagOrType(searchKeyword, contentKeyword, hashtagKeyword, pageable);
        }

        return questList;
    }

    // GET /api/board/quest/save
    @GetMapping("/quest/save")
    public QuestDTO saveForm() {
        return new QuestDTO(); // 기본 구조의 questDTO 반환
    }

    // POST /api/board/quest/save
    @PostMapping(value = "/quest/save", consumes = {"multipart/form-data"})
    public ResponseEntity<QuestDTO> save(@ModelAttribute QuestDTO questDTO) throws IOException {
        questService.save(questDTO);
        return ResponseEntity.ok(questDTO); // 200 OK
    }

    // GET /api/board/quest/{id}
    @GetMapping("/quest/{id}")
    public QuestDTO findById(@PathVariable Long id) {
        return questService.findByID(id);
    }

    // GET /api/board/quest/update/{id} (업데이트 폼 요청)
    @GetMapping("/quest/update/{id}")
    public ResponseEntity<QuestDTO> updateForm(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        QuestDTO questDTO = questService.findByID(id, userId);
        return ResponseEntity.ok(questDTO);
    }


    // POST /api/board/quest/update
    @PostMapping("/quest/update")
    public QuestDTO update(@RequestBody QuestDTO questDTO) {
        return questService.update(questDTO); // 업데이트된 questDTO 반환
    }

    // DELETE /api/board/quest/delete/{id}
    @DeleteMapping("/quest/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        boolean isDeleted = questService.delete(id, userId);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "사용자 권한이 없습니다.");
        }
    }

    // POST /api/board/quest/{id}/like
    @PostMapping("/quest/{id}/like")
    public ResponseEntity<Void> likeQuest(
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") String userId) {
        questService.increaseLike(id);
        return ResponseEntity.ok().build(); // 200 OK
    }

    // POST /api/board/quest/{id}/scrap
    @PostMapping("/quest/{id}/scrap")
    public ResponseEntity<Void> scrapQuest(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        questService.toggleScrap(id);
        return ResponseEntity.ok().build(); // 200 OK
    }


    @GetMapping("/quest/top-liked")
    public ResponseEntity<List<QuestDTO>> getTopLikedFrees() {
        List<QuestDTO> topLikedFrees = questService.getTopLikedFrees();
        if (topLikedFrees.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(topLikedFrees); // 200 OK
    }


    @GetMapping("/quest/sort-by-likes")
    public Page<QuestDTO> sortByLikes(
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "quest_like"));
        Page<QuestDTO> questList;

        // 검색 파라미터가 없으면 단순히 좋아요 순 정렬
        if ((searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty())
        ) {
            questList =  questService.sortByLikes(pageable);
            return questList;
        } else {
            // 검색 파라미터가 있으면 검색과 함께 좋아요 순 정렬
            return questService.searchAndSortByLikes(searchKeyword, contentKeyword, hashtagKeyword, pageable);
        }
    }

    // POST /api/board/quest/{id}/comments/add
    @PostMapping("/quest/{id}/comments/add")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long id,
                                                 @RequestParam(required = false) Long parentCommentId,
                                                 @RequestParam String content,
                                                 HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(content);
        commentDTO.setUserId(userId);
        commentDTO.setTargetType("quest");
        commentDTO.setTargetId(id);
        commentDTO.setParentCommentId(parentCommentId);
        commentService.addComment(commentDTO);
        return ResponseEntity.ok(commentDTO);
    }

    // POST /api/board/quest/{id}/comments/{commentId}/delete
    @PostMapping("/quest/{id}/comments/{commentId}/delete")
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


    // GET /api/board/quest/{id}/comments
    @GetMapping("/quest/{id}/comments")
    public ResponseEntity<Page<CommentDTO>> getComments(@PathVariable Long id,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CommentDTO> comments = commentService.getComments("quest", id, pageable);
        return ResponseEntity.ok(comments);
    }

}
