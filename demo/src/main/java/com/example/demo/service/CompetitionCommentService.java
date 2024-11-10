// src/main/java/com/example/demo/service/CompetitionCommentService.java
package com.example.demo.service;

import com.example.demo.dto.CompetitionCommentDTO;
import com.example.demo.entity.CompetitionCommentEntity;
import com.example.demo.entity.CompetitionEntity;
import com.example.demo.repository.CompetitionCommentRepository;
import com.example.demo.repository.CompetitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionCommentService {

    private final CompetitionCommentRepository competitionCommentRepository;
    private final CompetitionRepository competitionRepository;

    @Transactional
    public CompetitionCommentDTO addComment(Long competitionId, CompetitionCommentDTO commentDTO) {
        CompetitionEntity competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("대회를 찾을 수 없습니다."));
        CompetitionCommentEntity comment = new CompetitionCommentEntity();
        comment.setContent(commentDTO.getContent());
        comment.setAuthor(commentDTO.getAuthor());
        comment.setCompetitionEntity(competition);
        CompetitionCommentEntity savedComment = competitionCommentRepository.save(comment);
        return CompetitionCommentDTO.fromEntity(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CompetitionCommentDTO> getCommentsByCompetitionId(Long competitionId) {
        CompetitionEntity competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("대회를 찾을 수 없습니다."));
        List<CompetitionCommentEntity> comments = competitionCommentRepository.findByCompetitionEntity(competition);
        return comments.stream().map(CompetitionCommentDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        CompetitionCommentEntity comment = competitionCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        competitionCommentRepository.delete(comment);
    }
}
