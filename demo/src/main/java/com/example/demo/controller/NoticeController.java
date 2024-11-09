package com.example.demo.controller;

import com.example.demo.dto.NoticeDTO;
import com.example.demo.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        // page와 size가 null이거나 음수일 경우 기본값을 설정
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 10; // 기본 페이지 크기 설정
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "notice_created_time"));
        Page<NoticeDTO> noticeList;

        // 검색 조건에 따라 검색
        if ((searchKeyword == null || searchKeyword.isEmpty()) && (contentKeyword == null || contentKeyword.isEmpty())) {
            noticeList = noticeService.paging(pageable);
        } else {
            noticeList = noticeService.searchByTitleOrContents(searchKeyword, contentKeyword, pageable);
        }

        // 페이지 네비게이션 계산
        int blockLimit = 3;
        int currentPage = noticeList.getNumber();
        int startPage = (currentPage / blockLimit) * blockLimit + 1;
        int endPage = Math.min(startPage + blockLimit - 1, noticeList.getTotalPages()); // 수정

// 모델에 필요한 속성 추가
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
}
