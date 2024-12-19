package com.example.demo.controller;


import com.example.demo.dto.FreeDTO;
import com.example.demo.service.FreeService;
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
public class FreeController {

    private final FreeService freeService;

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
    @PostMapping("/free/save")
    public FreeDTO save(@RequestBody FreeDTO freeDTO) throws IOException {
        freeService.save(freeDTO);
        return freeDTO; // 저장된 CodingDTO 반환
    }

    // GET /api/board/coding/{id}
    @GetMapping("/free/{id}")
    public FreeDTO findById(@PathVariable Long id) {
        return freeService.findByID(id);
    }

    // GET /api/board/coding/update/{id}
    @GetMapping("/free/update/{id}")
    public FreeDTO updateForm(@PathVariable Long id) {
        return freeService.findByID(id);
    }

    // POST /api/board/coding/update
    @PostMapping("/free/update")
    public FreeDTO update(@RequestBody FreeDTO freeDTO) {
        return freeService.update(freeDTO); // 업데이트된 CodingDTO 반환
    }

    // DELETE /api/board/coding/delete/{id}
    @DeleteMapping("/free/delete/{id}")
    public void delete(@PathVariable Long id) {
        freeService.delete(id);
    }

    // POST /api/board/coding/{id}/like
    @PostMapping("/free/{id}/like")
    public void likeFree(@PathVariable Long id) {
        freeService.increaseLike(id);
    }


    // POST /api/board/coding/{id}/scrap
    @PostMapping("/free/{id}/scrap")
    public void scrapFree(@PathVariable Long id) {
        freeService.toggleScrap(id);
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

}
