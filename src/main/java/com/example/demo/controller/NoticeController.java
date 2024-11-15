package com.example.demo.controller;

import com.example.demo.dto.NoticeDTO;
import com.example.demo.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class NoticeController {
    private final NoticeService noticeService;

    // GET /api/board/notice
    @GetMapping("/notice")
    public ResponseEntity<Page<NoticeDTO>> paging(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "contentKeyword", required = false) String contentKeyword) {

        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));
        Page<NoticeDTO> noticeList;

        if ((searchKeyword == null || searchKeyword.isEmpty()) && (contentKeyword == null || contentKeyword.isEmpty())) {
            noticeList = noticeService.paging(pageable);
        } else {
            noticeList = noticeService.searchByTitleOrContents(searchKeyword, contentKeyword, pageable);
        }

        return ResponseEntity.ok(noticeList);
    }

    // POST /api/board/notice/save
    @PostMapping("/notice/save")
    public ResponseEntity<NoticeDTO> save(@RequestBody NoticeDTO noticeDTO) throws IOException {
        noticeService.save(noticeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeDTO);
    }

    // GET /api/board/notice/{id}
    @GetMapping("/notice/{id}")
    public ResponseEntity<NoticeDTO> findById(@PathVariable Long id) {
        NoticeDTO noticeDTO = noticeService.findByID(id);
        return ResponseEntity.ok(noticeDTO);
    }

    // POST /api/board/notice/update
    @PostMapping("/notice/update")
    public ResponseEntity<NoticeDTO> update(@RequestBody NoticeDTO noticeDTO) {
        NoticeDTO updatedNotice = noticeService.update(noticeDTO);
        return ResponseEntity.ok(updatedNotice);
    }

    // DELETE /api/board/notice/delete/{id}
    @DeleteMapping("/notice/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/board/notice/{id}/pin-toggle
    @PostMapping("/notice/{id}/pin-toggle")
    public ResponseEntity<Boolean> togglePin(@PathVariable Long id) {
        try {
            boolean isPinned = noticeService.togglePin(id);
            return ResponseEntity.ok(isPinned);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
