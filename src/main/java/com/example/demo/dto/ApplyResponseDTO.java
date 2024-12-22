package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyResponseDTO {
    private Long id;
    private String applyUserId;
    private boolean accept;
    private Long studyId;

    public ApplyResponseDTO(Long id, String applyUserId, boolean accept, Long studyId) {
        this.id = id;
        this.applyUserId = applyUserId;
        this.accept = accept;
        this.studyId = studyId;
    }
}