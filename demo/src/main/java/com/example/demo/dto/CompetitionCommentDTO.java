// src/main/java/com/example/demo/dto/CompetitionCommentDTO.java
package com.example.demo.dto;

import com.example.demo.entity.CompetitionCommentEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompetitionCommentDTO {
    private Long id;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private Long competitionId;

    public static CompetitionCommentDTO fromEntity(CompetitionCommentEntity entity) {
        CompetitionCommentDTO dto = new CompetitionCommentDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setAuthor(entity.getAuthor());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setCompetitionId(entity.getCompetitionEntity().getId());
        return dto;
    }
}

