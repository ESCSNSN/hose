package com.example.demo.service;


import com.example.demo.dto.CompetitionDTO;
import com.example.demo.dto.FreeDTO;
import com.example.demo.dto.MainCompetitionDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.CompetitionFileRepository;
import com.example.demo.repository.CompetitionLikeRepository;
import com.example.demo.repository.CompetitionRepository;
import com.example.demo.repository.CompetitionScrapRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetitionService {
    private final CompetitionRepository competitionRepository;
    private final CompetitionFileRepository competitionFileRepository;
    private final CompetitionLikeRepository competitionLikeRepository;
    private final CompetitionScrapRepository competitionScrapRepository;



    public void save(CompetitionDTO competitionDTO) throws IOException {

        if(competitionDTO.getCompetitionFile() == null ||competitionDTO.getCompetitionFile().isEmpty()){
            CompetitionEntity competitionEntity = CompetitionEntity.toSaveEntity(competitionDTO);
            competitionRepository.save(competitionEntity);

        }

        else {

            CompetitionEntity competitionEntity = CompetitionEntity.toSaveFileEntity(competitionDTO);
            Long savedId = competitionRepository.save(competitionEntity).getId();
            CompetitionEntity board = competitionRepository.findById(savedId).get();
            for (MultipartFile competitionFile : competitionDTO.getCompetitionFile()) {


                String originalFilename = competitionFile.getOriginalFilename(); // 2.
                String storedFileName = System.currentTimeMillis() + "_" + originalFilename; // 3.
                String savePath = "C:/springboot_img/" + storedFileName; // 4. C:/springboot_img/9802398403948_내사진.jpg
//            String savePath = "/Users/사용자이름/springboot_img/" + storedFileName; // C:/springboot_img/9802398403948_내사진.jpg
                competitionFile.transferTo(new File(savePath)); // 5.
                CompetitionFileEntity competitionFileEntity = CompetitionFileEntity.toCompetitionFileEntity(board, originalFilename, storedFileName);
                competitionFileRepository.save(competitionFileEntity);
            }

        }

    }

    @Transactional
    public List<CompetitionDTO> finAll() {
        List<CompetitionEntity> competitionEntityList = competitionRepository.findAll();
        List<CompetitionDTO> competitionDTOList = new ArrayList<>();
        for (CompetitionEntity competitionEntity : competitionEntityList) {
            competitionDTOList.add(CompetitionDTO.toCompetitionDTO(competitionEntity));
        }
        return competitionDTOList;
    }



    @Transactional
    public CompetitionDTO findByID(Long id) {
        Optional<CompetitionEntity> optionalCompetitionEntity = competitionRepository.findById(id);
        if(optionalCompetitionEntity.isPresent()) {
            CompetitionEntity competitionEntity = optionalCompetitionEntity.get();
            CompetitionDTO competitionDTO = CompetitionDTO.toCompetitionDTO(competitionEntity);
            return competitionDTO;
        }
        else {
            return null;
        }
    }

    @Transactional
    public CompetitionDTO findByID(Long id, String userId) {
        CompetitionEntity competition = competitionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "퀘스트를 찾을 수 없습니다."));

        if (!competition.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "퀘스트에 대한 접근 권한이 없습니다.");
        }
        CompetitionDTO competitionDTO = CompetitionDTO.toCompetitionDTO(competition);
        return competitionDTO;
    }

    public CompetitionDTO update(CompetitionDTO competitionDTO) {
        CompetitionEntity competitionEntity = CompetitionEntity.toUpdatedEntity(competitionDTO);
        competitionRepository.save(competitionEntity);
        return findByID(competitionDTO.getId());
    }

    @Transactional
    public boolean delete(Long id, String userId) {
        Optional<CompetitionEntity> optionalQuest = competitionRepository.findById(id);
        if (optionalQuest.isPresent()) {
            CompetitionEntity free = optionalQuest.get();
            if (free.getUserId().equals(userId)) {
                competitionRepository.delete(free);
                return true;
            }
        }
        return false;
    }


    public void deleteByAdmin(Long id) {
        competitionRepository.deleteById(id);
    }

    public Page<CompetitionDTO> paging(Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0); // 페이지가 음수일 경우 0으로 설정
        int pageLimit = 5; // 한 페이지에 보여줄 글 갯수

        // pageable을 사용해 페이지와 정렬을 설정
        Page<CompetitionEntity> competitionEntities = competitionRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        // NoticeEntity를 NoticeDTO로 변환
        return competitionEntities.map(competition -> new CompetitionDTO(
                competition.getId(),
                competition.getCompetitiontitle(),
                competition.getCompetitionCreatedTime(),
                competition.getCompetitionLike(),
                competition.getScrap()
        ));
    }


    @Transactional
    public Page<CompetitionDTO> searchByTitleOrContents(String title, String content, String hashtag, Pageable pageable) {
        Page<CompetitionEntity> competitionEntities = competitionRepository.findByTitleOrContentsContaining(title, content,hashtag, pageable);

        // Lazy-loaded 컬렉션을 초기화
        competitionEntities.forEach(notice -> notice.getCompetitionFileEntityList().size());

        return competitionEntities.map(CompetitionDTO::toCompetitionDTO);
    }

    @Transactional
    public void toggleLike(Long competitionId, String userId) {
        CompetitionEntity competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + competitionId));

        if (competitionLikeRepository.existsByUserIdAndCompetitionEntityId(userId, competitionId)) {
            // 좋아요가 이미 존재하면 삭제
            CompetitionLikeEntity competitionLike = competitionLikeRepository.findByUserIdAndCompetitionEntityId(userId, competitionId)
                    .orElseThrow(() -> new IllegalArgumentException("Like not found"));
            competitionLikeRepository.delete(competitionLike);

            // 좋아요 수 감소
            if (competition.getCompetitionLike() > 0) {
                competition.setCompetitionLike(competition.getCompetitionLike() - 1);
                competitionRepository.save(competition);
            }
        } else {
            // 좋아요가 없으면 추가
            CompetitionLikeEntity newLike = CompetitionLikeEntity.toCompetitionLikeEntity(competition, userId);
            competitionLikeRepository.save(newLike);

            // 좋아요 수 증가
            competition.setCompetitionLike(competition.getCompetitionLike() + 1);
            competitionRepository.save(competition);
        }
    }

    @Transactional
    public boolean hasUserLikedFree(Long competitionId, String userId) {
        return competitionLikeRepository.existsByUserIdAndCompetitionEntityId(userId, competitionId);
    }

    @Transactional
    public boolean toggleScrap(Long competitionId, String userId) {

        CompetitionEntity competition= competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + competitionId));

        if (competitionScrapRepository.existsByUserIdAndCompetitionEntityId(userId, competitionId)) {
            // 스크랩이 이미 존재하면 삭제
            CompetitionScrapEntity scrap = competitionScrapRepository.findByUserIdAndCompetitionEntityId(userId, competitionId)
                    .orElseThrow(() -> new IllegalArgumentException("Scrap not found"));
            competitionScrapRepository.delete(scrap);

            // 스크랩 수 감소
            if (competition.getScrap() > 0) {
                competition.setScrap(competition.getScrap() - 1);
                competitionRepository.save(competition);
            }
            return false; // 스크랩 해제됨
        } else {
            // 스크랩이 없으면 추가
            CompetitionScrapEntity newScrap = CompetitionScrapEntity.toScrapEntity(competition, userId);
            competitionScrapRepository.save(newScrap);

            // 스크랩 수 증가
            competition.setScrap(competition.getScrap() + 1);
            competitionRepository.save(competition);
            return true; // 스크랩 추가됨
        }
    }

    @Transactional
    public boolean hasUserScrappedCompetition(Long competitionId, String userId) {
        return competitionScrapRepository.existsByUserIdAndCompetitionEntityId(userId, competitionId);
    }

    @Transactional
    public List<CompetitionDTO> getScrappedCompetition(String userId, Long lastId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        List<CompetitionScrapEntity> scraps;

        if (lastId != null) {
            scraps = competitionScrapRepository.findTopByUserIdAndIdLessThan(userId, lastId, pageRequest);
        } else {
            scraps = competitionScrapRepository.findTopByUserIdAndIdLessThan(userId, Long.MAX_VALUE, pageRequest);
        }

        return scraps.stream()
                .map(scrap -> {
                    CompetitionEntity competition = scrap.getCompetitionEntity();
                    return new CompetitionDTO(
                            competition.getId(),
                            competition.getCompetitiontitle(),
                            competition.getCompetitionCreatedTime(),
                            competition.getCompetitionLike(),
                            competition.getScrap()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<CompetitionDTO> searchAndSortByLikes(String searchKeyword, String contentKeyword, String hashtagKeyword, Pageable pageable) {
        Page<CompetitionEntity> competitionEntities = competitionRepository.findByTitleOrContentsContaining(
                searchKeyword, contentKeyword, hashtagKeyword, pageable);

        // Lazy-loaded 컬렉션 초기화 (필요 시)
        competitionEntities.forEach(free -> free.getCompetitionFileEntityList().size());

        return competitionEntities.map(CompetitionDTO::toCompetitionDTO);
    }


    @Transactional
    public Page<CompetitionDTO> sortByLikes(Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0); // 페이지가 음수일 경우 0으로 설정
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수

        Pageable pageRequest = PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "competitionLike")); // 필드 이름 확인

        Page<CompetitionEntity> competitionEntities = competitionRepository.findAll(pageRequest);

        return competitionEntities.map(competition -> new CompetitionDTO(
                competition.getId(),
                competition.getCompetitiontitle(),
                competition.getCompetitionCreatedTime(),
                competition.getCompetitionLike(),
                competition.getScrap()
        ));
    }

    @Transactional
    public List<MainCompetitionDTO> getTop3Competitions() {
        PageRequest pageable = PageRequest.of(0, 3);
        List<CompetitionEntity> competitions = competitionRepository.findTop3CompetitionsWithFiles(pageable);
        return competitions.stream()
                .map(MainCompetitionDTO::toMainCompetitionDTO)
                .collect(Collectors.toList());
    }

    // 기존 코드에 추가
    @Transactional
    public List<CompetitionDTO> findAllByUserId(String userId) {
        List<CompetitionEntity> competitionEntities = competitionRepository.findByUserId(userId);
        return competitionEntities.stream()
                .map(CompetitionDTO::toCompetitionDTO)
                .collect(Collectors.toList());
    }

}
