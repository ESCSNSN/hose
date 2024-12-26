package com.example.demo.controller;

import com.example.demo.dto.CodingDTO;
import com.example.demo.dto.CompetitionDTO;
import com.example.demo.dto.FreeDTO;
import com.example.demo.dto.NoticeDTO;
import com.example.demo.service.CodingService;
import com.example.demo.service.CompetitionService;
import com.example.demo.service.FreeService;
import com.example.demo.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final CodingService codingService;
    private final CompetitionService competitionService;
    private final FreeService freeService;
    private final NoticeService noticeService;

    @GetMapping("/my-posts")
    public Map<String, Object> getMyPosts(HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");

        Map<String, Object> response = new HashMap<>();

        List<CodingDTO> myCodings = codingService.findAllByUserId(userId);
        List<CompetitionDTO> myCompetitions = competitionService.findAllByUserId(userId);
        List<FreeDTO> myFrees = freeService.findAllByUserId(userId);
        List<NoticeDTO> myNotices = noticeService.findAllByUserId(userId);

        response.put("codings", myCodings);
        response.put("competitions", myCompetitions);
        response.put("frees", myFrees);
        response.put("notices", myNotices);

        return response;
    }
}
