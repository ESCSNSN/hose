package com.example.demo.dto;

import com.example.demo.entity.GraduateEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MainGraduateDTO {

        private Long id;
        @JsonIgnore
        private String userID;
        private String boardID;
        private String graduateId;
        private String graduateTitle;
        private String graduateContents;
        private String graduateHashtag;


        private LocalDateTime graduateCreatedTime;
        private LocalDateTime graduateUpdatedTime;

        private int scrap;
        private int graduateLike;


        public static MainGraduateDTO toMainGraduateDTO(GraduateEntity graduateEntity) {
            MainGraduateDTO mainGraduateDTO = new MainGraduateDTO();
            mainGraduateDTO.setId(graduateEntity.getId());
            mainGraduateDTO.setUserID(graduateEntity.getUserId());
            mainGraduateDTO.setBoardID(graduateEntity.getBoardId());
            mainGraduateDTO.setGraduateId(graduateEntity.getGraduateId());
            mainGraduateDTO.setGraduateTitle(graduateEntity.getGraduatetitle());
            mainGraduateDTO.setGraduateContents(graduateEntity.getGraduatecontents());
            mainGraduateDTO.setGraduateHashtag(graduateEntity.getGraduatehashtag());
            mainGraduateDTO.setGraduateCreatedTime(graduateEntity.getGraduateCreatedTime());
            mainGraduateDTO.setGraduateUpdatedTime(graduateEntity.getGraduateUpdatedTime());
            mainGraduateDTO.setGraduateLike(graduateEntity.getGraduateLike());
            mainGraduateDTO.setScrap(graduateEntity.getScrap());

            return mainGraduateDTO;
        }
    }

