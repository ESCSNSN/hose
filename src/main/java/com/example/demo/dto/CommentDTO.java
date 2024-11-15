package com.example.demo.dto;

import com.example.demo.entity.CommentEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CommentDTO {
    private Long id;
    private String content;
    private String userId;
    private Long parentCommentId;
    private String targetType;
    private Long targetId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentDTO> replies;

    // 기본 생성자
    public CommentDTO() {}

    // 엔티티를 DTO로 변환하는 생성자
    public CommentDTO(CommentEntity entity) {
        this.id = entity.getId();
        this.content = entity.getContent();
        this.userId = entity.getUserId();
        this.targetType = entity.getTargetType();
        this.targetId = entity.getTargetId();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
        if (entity.getParentComment() != null) {
            this.parentCommentId = entity.getParentComment().getId();
        }
    }
}
