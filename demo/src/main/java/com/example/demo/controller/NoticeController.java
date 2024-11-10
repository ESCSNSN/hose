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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping("/notice")
    public String paging(@RequestParam(value = "page", required = false) Integer page,
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

        int blockLimit = 3;
        int currentPage = noticeList.getNumber();
        int startPage = (currentPage / blockLimit) * blockLimit + 1;
        int endPage = Math.min(startPage + blockLimit - 1, noticeList.getTotalPages());

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("contentKeyword", contentKeyword);

        return "notice";
    }





    @GetMapping("/notice/save")
    public String saveForm() {
        return "Nsave";
    }

    @PostMapping("/notice/save")
    public String save(@ModelAttribute NoticeDTO noticeDTO) throws IOException {
        noticeService.save(noticeDTO);
        return "redirect:/board/notice";
    }

    @GetMapping("/notice/{id}")
    public String findById(@PathVariable Long id, Model model,
                           @RequestParam(value = "page", defaultValue = "0") int page) {

        NoticeDTO noticeDTO = noticeService.findByID(id);
        model.addAttribute("notice", noticeDTO);
        model.addAttribute("page", page);
        return "Ndetail";
    }

    @GetMapping("/notice/update/{id}")
    public String updateForm(@PathVariable Long id, Model model,
                             @RequestParam(value = "page", defaultValue = "0") int page) {
        NoticeDTO noticeDTO = noticeService.findByID(id);
        model.addAttribute("noticeUpdate", noticeDTO);
        model.addAttribute("page", page);
        return "Nupdate";
    }

    @PostMapping("/notice/update")
    public String update(@ModelAttribute NoticeDTO noticeDTO, Model model) {
        NoticeDTO notice = noticeService.update(noticeDTO);
        model.addAttribute("notice", notice);
        return "Ndetail";
    }

    @GetMapping("/notice/delete/{id}")
    public String delete(@PathVariable Long id) {
        noticeService.delete(id);
        return "redirect:/board/notice";
    }


    @PostMapping("/notice/{id}/pin-toggle")
    public ResponseEntity<Boolean> togglePin(@PathVariable Long id) {
        boolean isPinned = noticeService.togglePin(id); // 핀 상태 반전 후 반환
        return ResponseEntity.ok(isPinned); // 새로운 핀 상태 반환
    }




}
