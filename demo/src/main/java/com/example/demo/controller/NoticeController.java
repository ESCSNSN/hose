package com.example.demo.controller;

import com.example.demo.dto.NoticeDTO;
import com.example.demo.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping("/notice")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model) {
        pageable.getPageNumber();

        Page<NoticeDTO> noticeList = noticeService.paging(pageable);
        int blockLimit = 5;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = ((startPage + blockLimit - 1) < noticeList.getTotalPages()) ? startPage + blockLimit - 1 : noticeList.getTotalPages();

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "notice";
    }


    @GetMapping("/notice/save")
    public String saveForm() {
        return "Nsave";
    }


    @PostMapping("/notice/save")
    public <noticeDTO> String save(@ModelAttribute NoticeDTO noticeDTO) throws IOException {
        noticeService.save(noticeDTO);
        return "redirect:/board/notice";
    }

    @GetMapping("/notice/{id}")
    public String findById(@PathVariable Long id, Model model,
                           @PageableDefault(page=1) Pageable pageable) {

        NoticeDTO  noticeDTO = noticeService.findByID(id);
        model.addAttribute("notice",noticeDTO);
        model.addAttribute("page", pageable.getPageNumber());
        return "Ndetail";
    }

    @GetMapping("/notice/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        NoticeDTO noticeDTO = noticeService.findByID(id);
        model.addAttribute("noticeUpdate",noticeDTO);
        return "Nupdate";
    }

    @PostMapping("/notice/update")
    public String update(@ModelAttribute NoticeDTO noticeDTO, Model model) {
        NoticeDTO notice =  noticeService.update(noticeDTO);
        model.addAttribute("notice",notice);
        return "Ndetail";
    }

    @GetMapping("/notice/delete")
    public String delete(@PathVariable Long id) {
        noticeService.delete(id);
        return "redirect:/board/notice";
    }


}
