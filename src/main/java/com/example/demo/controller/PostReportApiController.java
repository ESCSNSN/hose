package com.example.demo.controller;

import com.example.demo.dto.PostReportDTO;
import com.example.demo.repository.CodingRepository;
import com.example.demo.repository.CompetitionRepository;
import com.example.demo.repository.FreeRepository;
import com.example.demo.repository.GraduateRepository;
import com.example.demo.repository.QuestRepository;
import com.example.demo.repository.StudyRepository;
import com.example.demo.service.CodingService;
import com.example.demo.service.CompetitionService;
import com.example.demo.service.GraduateService;
import com.example.demo.service.PostReportService;
import com.example.demo.service.QuestService;
import com.example.demo.service.StudyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board")
public class PostReportApiController {

    @Autowired
    private PostReportService postReportService;
    private CodingService codingService;
    private StudyService studyService;
    private QuestService questService;
    private FreeController freeController;
    private GraduateService graduateService;
    private CompetitionService competitionService;
    @Autowired
    private CodingRepository codingRepository;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private QuestRepository questRepository;
    @Autowired
    private FreeRepository freeRepository;
    @Autowired
    private GraduateRepository graduateRepository;
    @Autowired
    private CompetitionRepository competitionRepository;

    /**
     * POST /api/board/{boardType}/{postId}/report
     */
    @PostMapping("/{boardType}/{postId}/report")
    public ResponseEntity<String> reportPost(@PathVariable Long postId,
                                             @PathVariable String boardType,
                                             @RequestParam String reason,
                                             HttpServletRequest request) {
        String reporterId = (String) request.getAttribute("username");
        if ("coding".equalsIgnoreCase(boardType)){
            postReportService.addReport(boardType,postId,reporterId, reason, codingRepository.findById(postId).get().getUserId());
        }
        else if ("studies".equalsIgnoreCase(boardType)){
            postReportService.addReport(boardType,postId,reporterId, reason, studyRepository.findById(postId).get().getUserId());
        }
        else if ("quest".equalsIgnoreCase(boardType)){
            postReportService.addReport(boardType,postId,reporterId, reason, questService.findByID(postId).getUserID());
        }
        else if ("free".equalsIgnoreCase(boardType)){
            postReportService.addReport(boardType,postId,reporterId, reason, freeRepository.findById(postId).get().getUserId());
        }
        else if ("graduate".equalsIgnoreCase(boardType)){
            postReportService.addReport(boardType,postId,reporterId, reason, graduateService.findByID(postId).getUserID());
        }
        else if ("competition".equalsIgnoreCase(boardType)){
            postReportService.addReport(boardType,postId,reporterId, reason, competitionService.findByID(postId).getUserId());
        }
        else{
                return ResponseEntity.badRequest().body("게시판 타입이 잘못되었습니다.");
        }

        return ResponseEntity.ok("게시글이 성공적으로 신고되었습니다.");
    }
}
