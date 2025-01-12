package com.example.demo.controller;

import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.CompetitionDTO;
import com.example.demo.dto.FreeDTO;
import com.example.demo.dto.QuestDTO;
import com.example.demo.entity.CodingEntity;
import com.example.demo.entity.CompetitionEntity;
import com.example.demo.exception.UnauthorizedDeletionException;
import com.example.demo.repository.CompetitionRepository;
import com.example.demo.service.CommentService;
import com.example.demo.service.CompetitionService;
// 불필요한 임포트 제거
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class CompetitionController {

    private final CompetitionService competitionService;
    private final CommentService commentService;
    private final CompetitionRepository competitionRepository;

    // GET /board/competition
    @GetMapping("/competition")
    public Page<CompetitionDTO> paging(
            HttpServletRequest request,
            @RequestParam(value = "page", required = false) Integer page,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size,
                                       @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                       @RequestParam(value = "contentKeyword", required = false) String contentKeyword,
                                       @RequestParam(value = "hashtagKeyword", required = false) String hashtagKeyword) {

        // page와 size가 null이거나 음수일 경우 기본값을 설정
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 10; // 기본 페이지 크기 설정
        }

        // 최신순 정렬을 위한 Pageable 설정
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "competition_created_time"));
        Page<CompetitionDTO> competitionList;
        String userId = (String) request.getAttribute("username");

        // 검색 조건에 따라 검색
        if ((searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty())) {
            competitionList = competitionService.paging(userId,pageable);
        } else {
            competitionList = competitionService.searchByTitleOrContents(userId,searchKeyword, contentKeyword, hashtagKeyword, pageable);
        }



        return competitionList;
    }


    // POST /api/board/coding/save
    @PostMapping(value = "/competition/save", consumes = {"multipart/form-data"})
    public ResponseEntity<CompetitionDTO> save(@ModelAttribute CompetitionDTO competitionDTO, HttpServletRequest request) throws IOException {
        String userId = (String) request.getAttribute("username");
        competitionDTO.setUserId(userId);
        competitionService.save(competitionDTO);
        return ResponseEntity.ok(competitionDTO); // 200 OK
    }

    @GetMapping("/competition/{id}")
    public ResponseEntity<CompetitionDTO> findById(@PathVariable Long id) {
        CompetitionDTO dto = competitionService.findByID(id);
        return ResponseEntity.ok(dto);
    }

    // GET /api/board/competition/update/{id} (업데이트 폼 요청)
    @GetMapping("/competition/update/{id}")
    public ResponseEntity<CompetitionDTO> updateForm(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        CompetitionDTO competitionDTO = competitionService.findByID(id, userId);
        return ResponseEntity.ok(competitionDTO);
    }

   @PostMapping("/competition/update")
   public CompetitionDTO update(@ModelAttribute CompetitionDTO competitionDTO) {
        return competitionService.update(competitionDTO);
   }


    // DELETE /api/board/notice/delete/{id}
    @DeleteMapping("/competition/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        boolean isDeleted = competitionService.delete(id, userId);
        if (isDeleted) {
            // 게시물에 해당하는 댓글 삭제
            commentService.deleteCommentsByTarget("Competition", id);
            return ResponseEntity.noContent().build();

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "사용자 권한이 없습니다.");
        }
    }


    // POST /api/board/quest/{id}/like
    @PostMapping("/competition/{id}/like")
    public ResponseEntity<String> toggleLikeQuest(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");

        try {
            competitionService.toggleLike(id, userId);
            return ResponseEntity.ok("Like toggled successfully");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 새로운 좋아요 상태 확인 엔드포인트
    @GetMapping("/competition/{id}/like-status")
    public ResponseEntity<Map<String, Object>> checkLikeStatus(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username"); // 또는 다른 방식으로 사용자 ID 가져오기

        boolean isLiked = competitionService.hasUserLikedFree(id, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", isLiked);
        return ResponseEntity.ok(response);
    }

    // 새로운 스크랩 토글 엔드포인트
    @PostMapping("/competition/{id}/scrap")
    public ResponseEntity<Map<String, Object>> toggleScrapQuest(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");

        try {
            boolean isScrapped = competitionService.toggleScrap(id, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Scrap toggled successfully");
            response.put("scrapped", isScrapped);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 새로운 좋아요 상태 확인 엔드포인트
    @GetMapping("/competition/{id}/scrap-status")
    public ResponseEntity<Map<String, Object>> checkScrapStatus(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");

        boolean isScraped = competitionService.hasUserScrappedCompetition(id, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("scrap", isScraped);
        return ResponseEntity.ok(response);
    }




    // POST /api/board/competition/{id}/comments/add
    @PostMapping("/competition/{id}/comments/add")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long id,
                                                 @RequestParam(required = false) Long parentCommentId,
                                                 @RequestParam String content,
                                                 HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(content);
        commentDTO.setUserId(userId);
        commentDTO.setTargetType("Competition");
        commentDTO.setTargetId(id);
        commentDTO.setParentCommentId(parentCommentId);
        commentService.addComment(commentDTO, "competition");
        return ResponseEntity.ok(commentDTO);
    }

    // POST /api/board/competition/{id}/comments/{commentId}/delete
    @PostMapping("/competition/{id}/comments/{commentId}/delete")
    public ResponseEntity<String> deleteComment(@PathVariable Long id,
                                                @PathVariable Long commentId,
                                                HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("username");
            commentService.deleteComment(commentId, userId);
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
        } catch (UnauthorizedDeletionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // GET /api/board/competition/{id}/comments
    @GetMapping("/competition/{id}/comments")
    public ResponseEntity<Page<CommentDTO>> getComments(@PathVariable Long id,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CommentDTO> comments = commentService.getComments("Competition", id, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/competition/top-liked")
    public ResponseEntity<List<CompetitionDTO>> getTopLikedCompetition(HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        List<CompetitionDTO> topLikedFrees = competitionService.getTopLikedCompetition(userId);
        if (topLikedFrees.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(topLikedFrees); // 200 OK
    }

    @GetMapping("/competition/sort-by-likes")
    public Page<CompetitionDTO> sortByLikes(
            HttpServletRequest request,
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
        String userId = (String) request.getAttribute("username");

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "competition_like"));
        Page<CompetitionDTO> competitionList;

        // 검색 파라미터가 없으면 단순히 좋아요 순 정렬
        if ((searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty())
        ) {
            competitionList =  competitionService.sortByLikes(userId,pageable);
            return competitionList;
        } else {
            // 검색 파라미터가 있으면 검색과 함께 좋아요 순 정렬
            return competitionService.searchAndSortByLikes(userId,searchKeyword, contentKeyword, hashtagKeyword, pageable);
        }
    }



}
