package com.example.demo.controller;

import com.example.demo.dto.CompetitionDTO;
import com.example.demo.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class CompetitionController {

    private final CompetitionService competitionService;

    // GET /api/board/competition
    @GetMapping("/competition")
    public Page<CompetitionDTO> paging(@RequestParam(value = "page", required = false) Integer page,
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

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "competition_created_time"));
        Page<CompetitionDTO> competitionList;

        if ((searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty())) {
            competitionList = competitionService.paging(pageable);
        } else {
            competitionList = competitionService.searchByTitleOrContents(searchKeyword, contentKeyword, hashtagKeyword, pageable);
        }

        return competitionList;
    }

    // POST /api/board/competition/save
    @PostMapping("/competition/save")
    public CompetitionDTO save(@RequestBody CompetitionDTO competitionDTO) throws IOException {
        competitionService.save(competitionDTO);
        return competitionDTO;
    }

    // GET /api/board/competition/{id}
    @GetMapping("/competition/{id}")
    public CompetitionDTO findById(@PathVariable Long id) {
        return competitionService.findByID(id);
    }

    // POST /api/board/competition/update
    @PostMapping("/competition/update")
    public CompetitionDTO update(@RequestBody CompetitionDTO competitionDTO) {
        return competitionService.update(competitionDTO);
    }

    // DELETE /api/board/competition/delete/{id}
    @DeleteMapping("/competition/delete/{id}")
    public void delete(@PathVariable Long id) {
        competitionService.delete(id);
    }

    // POST /api/board/competition/{id}/like
    @PostMapping("/competition/{id}/like")
    public void likeCompetition(@PathVariable Long id) {
        competitionService.toggleLike(id);
    }

    // POST /api/board/competition/{id}/scrap
    @PostMapping("/competition/{id}/scrap")
    public void scrapCompetition(@PathVariable Long id) {
        competitionService.toggleScrap(id);
    }
}
