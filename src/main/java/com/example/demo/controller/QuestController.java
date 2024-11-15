package com.example.demo.controller;


import com.example.demo.dto.QuestDTO;
import com.example.demo.service.QuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class QuestController {

    private final QuestService questService;

    // GET /api/board/coding
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

    // GET /api/board/coding/save
    @GetMapping("/quest/save")
    public QuestDTO saveForm() {
        return new QuestDTO(); // 기본 구조의 CodingDTO 반환
    }

    // POST /api/board/coding/save
    @PostMapping("/quest/save")
    public QuestDTO save(@RequestBody QuestDTO questDTO) throws IOException {
        questService.save(questDTO);
        return questDTO; // 저장된 CodingDTO 반환
    }

    // GET /api/board/coding/{id}
    @GetMapping("/quest/{id}")
    public QuestDTO findById(@PathVariable Long id) {
        return questService.findByID(id);
    }

    // GET /api/board/coding/update/{id}
    @GetMapping("/free/quest/{id}")
    public QuestDTO updateForm(@PathVariable Long id) {
        return questService.findByID(id);
    }

    // POST /api/board/coding/update
    @PostMapping("/quest/update")
    public QuestDTO update(@RequestBody QuestDTO questDTO) {
        return questService.update(questDTO); // 업데이트된 CodingDTO 반환
    }

    // DELETE /api/board/coding/delete/{id}
    @DeleteMapping("/quest/delete/{id}")
    public void delete(@PathVariable Long id) {
        questService.delete(id);
    }

    // POST /api/board/coding/{id}/like
    @PostMapping("/quest/{id}/like")
    public void likeFree(@PathVariable Long id) {
        questService.increaseLike(id);
    }


    // POST /api/board/coding/{id}/scrap
    @PostMapping("/quest/{id}/scrap")
    public void scrapQuest(@PathVariable Long id) {
        questService.toggleScrap(id);
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

}
