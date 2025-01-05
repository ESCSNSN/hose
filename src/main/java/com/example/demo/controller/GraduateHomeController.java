package com.example.demo.controller;

import com.example.demo.dto.MainFreeDTO;
import com.example.demo.dto.MainGraduateDTO;
import com.example.demo.dto.MainQuestDTO;
import com.example.demo.service.FreeService;
import com.example.demo.service.GraduateService;
import com.example.demo.service.QuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board/graduate")
public class GraduateHomeController {

    private final QuestService questService;
    private final FreeService freeService;
    private final GraduateService graduateService;

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

    @GetMapping("/top-free")
    public ResponseEntity<List<MainGraduateDTO>> getTop3FreeGraduates() {
        List<MainGraduateDTO> topFreeGraduates = graduateService.getTop3FreeGraduates();
        if (topFreeGraduates.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(topFreeGraduates); // 200 OK
    }

    @GetMapping("/top-quest")
    public ResponseEntity<List<MainGraduateDTO>> getTop3QuestGraduates() {
        List<MainGraduateDTO> topQuestGraduates = graduateService.getTop3QuestGraduates();
        if (topQuestGraduates.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(topQuestGraduates); // 200 OK
    }

}
