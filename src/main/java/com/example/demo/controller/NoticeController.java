package com.example.demo.controller;

import com.example.demo.dto.NoticeDTO;
import com.example.demo.service.NoticeService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping("/notice")
    public ResponseEntity<Page<NoticeDTO>> paging(@RequestParam(value = "page", required = false) Integer page,
                                                  @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                  Model model,
                                                  @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                                  @RequestParam(value = "contentKeyword", required = false) String contentKeyword) {

        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<NoticeDTO> noticeList;

        if ((searchKeyword == null || searchKeyword.isEmpty()) && (contentKeyword == null || contentKeyword.isEmpty())) {
            noticeList = noticeService.paging(pageable); // 검색어 없을 때 기본 페이징
        } else {
            noticeList = noticeService.searchByTitleOrContents(searchKeyword, contentKeyword, pageable); // 검색어 있을 때 검색 결과
        }



        return ResponseEntity.ok(noticeList);
    }





    @PostMapping(value = "/notice/save", consumes = {"multipart/form-data"})
    public ResponseEntity<NoticeDTO> save(@ModelAttribute NoticeDTO noticeDTO, HttpServletRequest request) throws IOException {
        String role = (String) request.getAttribute("role");
        if (!"admin".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied: Admin Role Required");
        }
        String userId = (String) request.getAttribute("username");
        noticeDTO.setUserId(userId);
        noticeService.save(noticeDTO);
        return ResponseEntity.ok(noticeDTO); // 200 OK
    }

    // GET /api/board/notice/{id}
    @GetMapping("/notice/{id}")
    public ResponseEntity<NoticeDTO> findById(@PathVariable Long id) {
        NoticeDTO noticeDTO = noticeService.findByID(id);
        return ResponseEntity.ok(noticeDTO);
    }

    // GET /api/board/notice/update/{id} (업데이트 폼 요청)
    @GetMapping("/notice/update/{id}")
    public ResponseEntity<NoticeDTO> updateForm(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        NoticeDTO noticeDTO = noticeService.findByID(id, userId);
        return ResponseEntity.ok(noticeDTO);
    }

    // POST /api/board/coding/update
    @PostMapping("/notice/update")
    public NoticeDTO update(@RequestBody NoticeDTO noticeDTO) {
        return noticeService.update(noticeDTO); // 업데이트된 CodingDTO 반환
    }

    // DELETE /api/board/notice/delete/{id}
    @DeleteMapping("/notice/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        boolean isDeleted = noticeService.delete(id, userId);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "사용자 권한이 없습니다.");
        }
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
