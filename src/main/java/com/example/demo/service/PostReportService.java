package com.example.demo.service;

import com.example.demo.dto.PostReportDTO;
import com.example.demo.entity.PostReportEntity;
import com.example.demo.repository.PostReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostReportService {

    @Autowired
    private PostReportRepository postReportRepository;

    public void addReport(PostReportDTO reportDTO) {
        PostReportEntity entity = new PostReportEntity(
                reportDTO.getPostId(),
                reportDTO.getReporterId(),
                reportDTO.getReason()
        );
        postReportRepository.save(entity);
    }

    // 추가적인 서비스 메서드가 필요하다면 여기에 작성
}
