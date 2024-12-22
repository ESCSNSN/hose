package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicantResponseDTO {
    private Long applyId;
    private String applyUserId;
    private boolean accept;
    private Long studyId;

    public ApplicantResponseDTO(Long applyId, String applyUserId, boolean accept, Long studyId) {
        this.applyId = applyId;
        this.applyUserId = applyUserId;
        this.accept = accept;
        this.studyId = studyId;
    }
}
