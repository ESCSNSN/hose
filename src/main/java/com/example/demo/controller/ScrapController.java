package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/board/scraps")
public class ScrapController {

    private final QuestService questService;
    private final FreeService freeService;
    private final CodingService codingService;
    private final CompetitionService competitionService;
    private final StudyService studyService;
    private final GraduateService graduateService;

    public ScrapController(QuestService questService, FreeService freeService, CodingService codingService, CompetitionService competitionService, StudyService studyService, GraduateService graduateService) {
        this.questService = questService;
        this.freeService = freeService;
        this.codingService = codingService;
        this.competitionService = competitionService;
        this.studyService = studyService;
        this.graduateService = graduateService;
    }

    /**
     * 사용자가 스크랩한 모든 퀘스트 목록을 가져옵니다. (더보기 기능 지원)
     *
     * @param request HTTP 요청 객체
     * @param limit 가져올 데이터 수 (기본값: 10)
     * @param lastId 마지막으로 받은 퀘스트의 ID (기본값: null)
     * @return 스크랩한 퀘스트 목록과 hasNext 플래그
     */
    @GetMapping("/quest")
    public ResponseEntity<Map<String, Object>> getMyScrappedQuests(
            HttpServletRequest request,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "lastId", required = false) Long lastId) {
        String userId = (String) request.getAttribute("username");

        List<QuestDTO> scrappedQuests = questService.getScrappedQuests(userId, lastId, limit);
        boolean hasNext = false;
        if (scrappedQuests.size() > limit) {
            hasNext = true;
            scrappedQuests.remove(scrappedQuests.size() - 1); // limit을 초과한 마지막 항목 제거
        }

        Map<String, Object> response = Map.of(
                "data", scrappedQuests,
                "hasNext", hasNext
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/free")
    public ResponseEntity<Map<String, Object>> getMyScrappedFree(
            HttpServletRequest request,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "lastId", required = false) Long lastId) {
        String userId = (String) request.getAttribute("username");

        List<FreeDTO> scrappedFree = freeService.getScrappedFree(userId, lastId, limit);
        boolean hasNext = false;
        if (scrappedFree.size() > limit) {
            hasNext = true;
            scrappedFree.remove(scrappedFree.size() - 1); // limit을 초과한 마지막 항목 제거
        }

        Map<String, Object> response = Map.of(
                "data", scrappedFree,
                "hasNext", hasNext
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/coding")
    public ResponseEntity<Map<String, Object>> getMyScrappedCoding(
            HttpServletRequest request,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "lastId", required = false) Long lastId) {
        String userId = (String) request.getAttribute("username");

        List<CodingDTO> scrappedCoding = codingService.getScrappedCoding(userId, lastId, limit);
        boolean hasNext = false;
        if (scrappedCoding.size() > limit) {
            hasNext = true;
            scrappedCoding.remove(scrappedCoding.size() - 1); // limit을 초과한 마지막 항목 제거
        }

        Map<String, Object> response = Map.of(
                "data", scrappedCoding,
                "hasNext", hasNext
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/competition")
    public ResponseEntity<Map<String, Object>> getMyScrappedCompetition(
            HttpServletRequest request,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "lastId", required = false) Long lastId) {
        String userId = (String) request.getAttribute("username");

        List<CompetitionDTO> scrappedCompetition = competitionService.getScrappedCompetition(userId, lastId, limit);
        boolean hasNext = false;
        if (scrappedCompetition.size() > limit) {
            hasNext = true;
            scrappedCompetition.remove(scrappedCompetition.size() - 1); // limit을 초과한 마지막 항목 제거
        }

        Map<String, Object> response = Map.of(
                "data", scrappedCompetition,
                "hasNext", hasNext
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/study")
    public ResponseEntity<StudyResponse> getScrappedQuests(
            HttpServletRequest request,
            @RequestParam(value = "studyId", required = false) String studyId,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        String userId = (String) request.getAttribute("username");


        // 서비스 호출하여 스크랩된 스터디 목록 조회
        StudyResponse response = studyService.getScrappedQuests(userId, lastId, studyId, limit);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/graduate")
    public ResponseEntity<Map<String, Object>> getScrappedGraduate(
            HttpServletRequest request,
            @RequestParam(value = "GraduateId", required = false) String GraduateId,
            @RequestParam(value = "lastId", required = false) Long lastId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        String userId = "202001685"; // 사용자 ID 가져오기


        List<GraduateDTO> scrappedGraduate = graduateService.getScrappedGraduate(userId, lastId,GraduateId ,limit);
        boolean hasNext = false;
        if (scrappedGraduate.size() > limit) {
            hasNext = true;
            scrappedGraduate.remove(scrappedGraduate.size() - 1); // limit을 초과한 마지막 항목 제거
        }

        Map<String, Object> response = Map.of(
                "data", scrappedGraduate,
                "hasNext", hasNext
        );

        return ResponseEntity.ok(response);
    }
}
