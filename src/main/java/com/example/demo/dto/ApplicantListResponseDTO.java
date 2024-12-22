package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApplicantListResponseDTO {
    private Long studyId;
    private String studyTitle;
    private List<ApplicantResponseDTO> applicants;

    public ApplicantListResponseDTO(Long studyId, String studyTitle, List<ApplicantResponseDTO> applicants) {
        this.studyId = studyId;
        this.studyTitle = studyTitle;
        this.applicants = applicants;
    }
}
