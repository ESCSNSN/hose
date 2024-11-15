package com.example.demo.controller;

import com.example.demo.dto.CompetitionDTO;
import com.example.demo.service.CompetitionService;
// 불필요한 임포트 제거
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
public class CompetitionController {

    private final CompetitionService competitionService;

    // GET /board/competition
    @GetMapping("/competition")
    public String paging(@RequestParam(value = "page", required = false) Integer page,
                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                         Model model,
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

        // 검색 조건에 따라 검색
        if ((searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty())) {
            competitionList = competitionService.paging(pageable);
        } else {
            competitionList = competitionService.searchByTitleOrContents(searchKeyword, contentKeyword, hashtagKeyword, pageable);
        }

        // 페이지 네비게이션 계산
        int blockLimit = 3;
        int currentPage = competitionList.getNumber(); // 0-based index
        int startPage = (currentPage / blockLimit) * blockLimit + 1;
        int endPage = Math.min(startPage + blockLimit - 1, competitionList.getTotalPages());

        // 모델에 필요한 속성 추가
        model.addAttribute("competitionList", competitionList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("contentKeyword", contentKeyword);
        model.addAttribute("hashtagKeyword", hashtagKeyword);

        return "competition";
    }

    // GET /board/competition/save
    @GetMapping("/competition/save")
    public String saveForm(Model model) {
        // BoardService 주입이 필요하다면 추가
        // 예: List<Board> boards = boardService.getAllBoards();
        // model.addAttribute("boards", boards);
        model.addAttribute("competitionDTO", new CompetitionDTO());
        return "Csave"; // 템플릿 이름에 맞게 변경
    }

    // POST /board/competition/save
    @PostMapping("/competition/save")
    public String save(@ModelAttribute CompetitionDTO competitionDTO) throws IOException {
        competitionService.save(competitionDTO);
        return "redirect:/board/competition";
    }

    // GET /board/competition/{id}
    @GetMapping("/competition/{id}")
    public String findById(@PathVariable Long id, Model model,
                           @RequestParam(value = "page", defaultValue = "0") int page) {

        CompetitionDTO competitionDTO = competitionService.findByID(id);
        model.addAttribute("competition", competitionDTO);
        model.addAttribute("page", page);
        return "Cdetail"; // 템플릿 이름에 맞게 변경
    }

    // GET /board/competition/update/{id}
    @GetMapping("/competition/update/{id}")
    public String updateForm(@PathVariable Long id, Model model,
                             @RequestParam(value = "page", defaultValue = "0") int page) {
        CompetitionDTO competitionDTO = competitionService.findByID(id);
        model.addAttribute("competitionUpdate", competitionDTO);
        model.addAttribute("page", page);
        // BoardService 주입이 필요하다면 추가
        // 예: List<Board> boards = boardService.getAllBoards();
        // model.addAttribute("boards", boards);
        return "Cupdate"; // 템플릿 이름에 맞게 변경
    }

    // POST /board/competition/update
    @PostMapping("/competition/update")
    public String update(@ModelAttribute CompetitionDTO competitionDTO, Model model) {
        CompetitionDTO competition = competitionService.update(competitionDTO);
        model.addAttribute("competition", competition);
        return "redirect:/board/competition"; // 성공 시 목록으로 리다이렉트
    }

    // GET /board/competition/delete/{id}
    @GetMapping("/competition/delete/{id}")
    public String delete(@PathVariable Long id) {
        competitionService.delete(id);
        return "redirect:/board/competition";
    }

    // POST /board/competition/{id}/like
    @PostMapping("/competition/{id}/like")
    public String likeCompetition(@PathVariable Long id, @RequestParam(value = "page", defaultValue = "0") int page) {
        competitionService.toggleLike(id);
        return "redirect:/board/competition/" + id + "?page=" + page;
    }

    // POST /board/competition/{id}/scrap
    @PostMapping("/competition/{id}/scrap")
    public String scrapCompetition(@PathVariable Long id, @RequestParam(value = "page", defaultValue = "0") int page) {
        competitionService.toggleScrap(id);
        return "redirect:/board/competition/" + id + "?page=" + page;

    }
}
