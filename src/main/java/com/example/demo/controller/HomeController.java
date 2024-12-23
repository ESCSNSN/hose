package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class HomeController {

    private final StudyService studyService;
    private final CodingService codingService;
    private final CompetitionService competitionService;
    private final QuestService questService;
    private final FreeService freeService;

    @GetMapping("/main/quest")
    public ResponseEntity<List<MainQuestDTO>> getTop3Quests() {
        List<MainQuestDTO> top3Quests = questService.getTop3Quests();
        return ResponseEntity.ok(top3Quests);
    }

    @GetMapping("/main/free")
    public ResponseEntity<List<MainFreeDTO>> getTop3FreePosts() {
        List<MainFreeDTO> top3FreePosts = freeService.getTop3FreePosts();
        return ResponseEntity.ok(top3FreePosts);
    }

    @GetMapping("/main/competition")
    public ResponseEntity<List<MainCompetitionDTO>> getTop3Competitions() {
        List<MainCompetitionDTO> top3Competitions = competitionService.getTop3Competitions();
        return ResponseEntity.ok(top3Competitions);
    }

    @GetMapping("/main/coding")
    public ResponseEntity<List<MainCodingDTO>> getTop2Codings() {
        List<MainCodingDTO> top2Codings = codingService.getTop2Codings();
        return ResponseEntity.ok(top2Codings);
    }

    /**
     * 부트캠프, 산업연계, 스터디 각 카테고리에서 상위 3개의 스터디 게시물을 조회하는 API 엔드포인트
     *
     * @return 부트캠프, 산업연계, 스터디 각각의 MainStudyDTO 리스트를 포함한 MainStudyResponseDTO
     */
    @GetMapping("/main/study")
    public ResponseEntity<MainStudyResponseDTO> getTopStudiesByCategories() {
        Map<String, List<MainStudyDTO>> topStudiesMap = studyService.getTop3StudiesByCategories();

        List<MainStudyDTO> bootcampStudies = topStudiesMap.getOrDefault("bootcamp", List.of());
        List<MainStudyDTO> industryStudies = topStudiesMap.getOrDefault("industry", List.of());
        List<MainStudyDTO> regularStudies = topStudiesMap.getOrDefault("study", List.of());

        MainStudyResponseDTO responseDTO = new MainStudyResponseDTO(bootcampStudies, industryStudies, regularStudies);
        return ResponseEntity.ok(responseDTO);
    }
}
