package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    private final CommentService commentService;
    private final StudyService studyService;

    @GetMapping("/my-posts")
    public Map<String, Object> getMyPosts(HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");

        List<UnifiedPostDTO> unifiedPosts = new ArrayList<>();

        // Coding Posts
        codingService.findAllByUserId(userId).forEach(coding -> {
            UnifiedPostDTO post = new UnifiedPostDTO();
            post.setId(coding.getId());
            post.setTitle(coding.getCodingTitle()); // coding_title 필드 매핑
            post.setCreateTime(coding.getCodingCreatedTime());  // 필요에 따라 다른 필드 매핑
            post.setType("coding");
            unifiedPosts.add(post);
        });

        // Competition Posts
        competitionService.findAllByUserId(userId).forEach(competition -> {
            UnifiedPostDTO post = new UnifiedPostDTO();
            post.setId(competition.getId());
            post.setTitle(competition.getCompetitionTitle()); // competition_title 필드 매핑
            post.setCreateTime(competition.getCompetitionCreatedTime()); // 필요에 따라 다른 필드 매핑
            post.setType("competition");
            unifiedPosts.add(post);
        });

        // Free Posts
        freeService.findAllByUserId(userId).forEach(free -> {
            UnifiedPostDTO post = new UnifiedPostDTO();
            post.setId(free.getId());
            post.setTitle(free.getFreeTitle()); // free_title 필드 매핑
            post.setCreateTime(free.getFreeCreatedTime()); // 필요에 따라 다른 필드 매핑
            post.setType("free");
            unifiedPosts.add(post);
        });

        // Notice Posts
        noticeService.findAllByUserId(userId).forEach(notice -> {
            UnifiedPostDTO post = new UnifiedPostDTO();
            post.setId(notice.getId());
            post.setTitle(notice.getNoticeTitle()); // notice_title 필드 매핑
            post.setCreateTime(notice.getNoticeCreatedTime()); // 필요에 따라 다른 필드 매핑
            post.setType("notice");
            unifiedPosts.add(post);
        });

        // Study Posts
        studyService.findAllByUserId(userId).forEach(study -> {
            UnifiedPostDTO post = new UnifiedPostDTO();
            post.setId(study.getId());
            post.setTitle(study.getStudyTitle()); // study_title 필드 매핑
            post.setCreateTime(study.getStudyCreatedTime()); // 필요에 따라 다른 필드 매핑
            post.setType("study");
            unifiedPosts.add(post);
        });


        return Map.of("posts", unifiedPosts);
    }

    @GetMapping("/my-comments")
    public List<CommentDTO> getMyComments(HttpServletRequest request) {
        String userId = (String) request.getAttribute("username");
        return commentService.findAllByUserId(userId);
    }

}
