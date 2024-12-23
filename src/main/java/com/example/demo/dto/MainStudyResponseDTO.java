package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MainStudyResponseDTO {
    private List<MainStudyDTO> bootcampStudies;
    private List<MainStudyDTO> industryStudies;
    private List<MainStudyDTO> regularStudies;
}
