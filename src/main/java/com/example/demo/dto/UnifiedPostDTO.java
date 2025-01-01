package com.example.demo.dto;

import java.time.LocalDateTime;

public class UnifiedPostDTO {
    private Long id;
    private String title;
    private LocalDateTime createTime;
    private String type; // 예: "coding", "competition", "free", "notice"

    // 기본 생성자
    public UnifiedPostDTO() {}

    // 모든 필드를 포함한 생성자
    public UnifiedPostDTO(Long id, String title, LocalDateTime createTime, String type) {
        this.id = id;
        this.title = title;
        this.createTime = createTime;
        this.type = type;
    }

    // Getter 및 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
