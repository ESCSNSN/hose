package com.example.demo.service;

import com.example.demo.dto.FreeDTO;
import com.example.demo.dto.GraduateDTO;
import com.example.demo.dto.MainGraduateDTO;
import com.example.demo.dto.QuestDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.GraduateLikeRepository;
import com.example.demo.repository.GraduateRepository;
import com.example.demo.repository.GraduateScrapRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GraduateService {


    private final GraduateRepository graduateRepository;
    private final GraduateLikeRepository graduateLikeRepository;
    private final GraduateScrapRepository graduateScrapRepository;

    public void save(GraduateDTO graduateDTO) throws IOException {
        System.out.println("GraduateDTO.getGraduateId(): " + graduateDTO.getGraduateId());
        GraduateEntity graduateEntity = GraduateEntity.toSaveEntity(graduateDTO);
        System.out.println("GraduateEntity.getGraduateId(): " + graduateEntity.getGraduateId());
        graduateRepository.save(graduateEntity);
    }



    @Transactional
    public GraduateDTO findByID(Long id) {
        Optional<GraduateEntity> optionalGraduateEntity = graduateRepository.findById(id);
        if(optionalGraduateEntity.isPresent()) {
            GraduateEntity graduateEntity = optionalGraduateEntity.get();
            GraduateDTO graduateDTO = GraduateDTO.toGraduateDTO(graduateEntity);
            return graduateDTO;
        }
        else {
            return null;
        }
    }


    @Transactional
    public GraduateDTO findByID(Long id, String userId) {
        GraduateEntity graduate = graduateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "퀘스트를 찾을 수 없습니다."));

        if (!graduate.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "퀘스트에 대한 접근 권한이 없습니다.");
        }
        GraduateDTO graduateDTO = GraduateDTO.toGraduateDTO(graduate);
        return graduateDTO;
    }

    public GraduateDTO update(GraduateDTO graduateDTO) {
        GraduateEntity graduateEntity = GraduateEntity.toUpdatedEntity(graduateDTO);
        graduateRepository.save(graduateEntity);
        return findByID(graduateDTO.getId());
    }

    @Transactional
    public boolean delete(Long id, String userId) {
        Optional<GraduateEntity> optionalGraduate = graduateRepository.findById(id);
        if (optionalGraduate.isPresent()) {
            GraduateEntity graduate = optionalGraduate.get();
            if (graduate.getUserId().equals(userId)) {
                graduateRepository.delete(graduate);
                return true;
            }
        }
        return false;
    }

    public Page<GraduateDTO> paging(Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0); // 페이지가 음수일 경우 0으로 설정
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수

        // pageable을 사용해 페이지와 정렬을 설정
        Page<GraduateEntity> graduateEntities = graduateRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));


        return graduateEntities.map(graduate -> new GraduateDTO(
                graduate.getId(),
                graduate.getGraduateId(),
                graduate.getGraduatetitle(),
                graduate.getGraduateCreatedTime(),
                graduate.getGraduateLike(),
                graduate.getScrap()
        ));
    }


    @Transactional
    public Page<GraduateDTO> searchByTitleOrContentOrHashtagOrType(String graduateId ,String title, String content, String hashtag,  Pageable pageable) {
        Page<GraduateEntity> graduateEntities = graduateRepository.findByTitleOrContentsContaining(graduateId,title, content, hashtag,  pageable);

        return graduateEntities.map(GraduateDTO::toGraduateDTO);
    }

    @Transactional
    public void toggleLike(Long graduateId, String userId) {
        GraduateEntity graduate = graduateRepository.findById(graduateId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + graduateId));

        if (graduateLikeRepository.existsByUserIdAndGraduateEntityId(userId, graduateId)) {
            // 좋아요가 이미 존재하면 삭제
            GraduateLikeEntity like = graduateLikeRepository.findByUserIdAndGraduateEntityId(userId, graduateId)
                    .orElseThrow(() -> new IllegalArgumentException("Like not found"));
            graduateLikeRepository.delete(like);

            // 좋아요 수 감소
            if (graduate.getGraduateLike() > 0) {
                graduate.setGraduateLike(graduate.getGraduateLike() - 1);
                graduateRepository.save(graduate);
            }
        } else {
            // 좋아요가 없으면 추가
            GraduateLikeEntity newLike = GraduateLikeEntity.toGraduateLikeEntity(graduate, userId);
            graduateLikeRepository.save(newLike);

            // 좋아요 수 증가
            graduate.setGraduateLike(graduate.getGraduateLike() + 1);
            graduateRepository.save(graduate);
        }
    }

    @Transactional
    public boolean hasUserLikedGraduate(Long freeId, String userId) {
        return graduateLikeRepository.existsByUserIdAndGraduateEntityId(userId, freeId);
    }


    @Transactional
    public boolean toggleScrap(Long graduateId, String userId) {
        GraduateEntity graduate = graduateRepository.findById(graduateId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + graduateId));

        if (graduateScrapRepository.existsByUserIdAndGraduateEntityId(userId, graduateId)) {
            // 스크랩이 이미 존재하면 삭제
            GraduateScrapEntity scrap = graduateScrapRepository.findByUserIdAndGraduateEntityId(userId, graduateId)
                    .orElseThrow(() -> new IllegalArgumentException("Scrap not found"));
            graduateScrapRepository.delete(scrap);

            // 스크랩 수 감소
            if (graduate.getScrap() > 0) {
                graduate.setScrap(graduate.getScrap() - 1);
                graduateRepository.save(graduate);
            }
            return false; // 스크랩 해제됨
        } else {
            // 스크랩이 없으면 추가
            GraduateScrapEntity newScrap = GraduateScrapEntity.toScrapEntity(graduate, userId);
            graduateScrapRepository.save(newScrap);

            // 스크랩 수 증가
            graduate.setScrap(graduate.getScrap() + 1);
            graduateRepository.save(graduate);
            return true; // 스크랩 추가됨
        }
    }

    @Transactional
    public boolean hasUserScrappedGraduate(Long graduateId, String userId) {
        return graduateScrapRepository.existsByUserIdAndGraduateEntityId(userId, graduateId);
    }


    @Transactional
    public List<GraduateDTO> getScrappedGraduate(String userId, Long lastGraduateId,String graduateId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        List<GraduateScrapEntity> scraps;

        if (lastGraduateId != null) {
            scraps = graduateScrapRepository.findByUserIdAndGraduateEntityIdLessThanOrderByGraduateEntityIdDesc(userId, lastGraduateId, graduateId, pageRequest);
        } else {
            scraps = graduateScrapRepository.findByUserIdOrderByGraduateEntityIdDesc(userId, graduateId, pageRequest);
        }

        return scraps.stream()
                .map(scrap -> {
                    GraduateEntity graduate = scrap.getGraduateEntity();
                    return new GraduateDTO(
                            graduate.getId(),
                            graduate.getGraduateId(),
                            graduate.getGraduatetitle(),
                            graduate.getGraduateCreatedTime(),
                            graduate.getGraduateLike(),
                            graduate.getScrap()
                            // 필요한 추가 필드
                    );
                })
                .collect(Collectors.toList());
    }

    public List<GraduateDTO> getTopLikedFrees() {
        int likeThreshold = 10;
        int limit = 3;
        PageRequest pageRequest = PageRequest.of(0, limit);

        List<GraduateEntity> topLikedEntities = graduateRepository.findByGraduateLikeGreaterThanEqualOrderByGraduateCreatedTimeDesc(likeThreshold, pageRequest);

        return topLikedEntities.stream()
                .map(graduate -> new GraduateDTO(
                        graduate.getId(),
                        graduate.getGraduateId(),
                        graduate.getGraduatetitle(),
                        graduate.getGraduateCreatedTime(),
                        graduate.getGraduateLike(),
                        graduate.getScrap()
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public Page<GraduateDTO> searchAndSortByLikes(String graduateId,String searchKeyword, String contentKeyword, String hashtagKeyword, Pageable pageable) {
        Page<GraduateEntity> graduateEntities = graduateRepository.findByTitleOrContentsContaining(
                graduateId,searchKeyword, contentKeyword, hashtagKeyword, pageable);



        return graduateEntities.map(GraduateDTO::toGraduateDTO);
    }


    @Transactional
    public Page<GraduateDTO> sortByLikes(Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0); // 페이지가 음수일 경우 0으로 설정
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수

        Pageable pageRequest = PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "graduateLike")); // 필드 이름 확인

        Page<GraduateEntity> graduateEntities = graduateRepository.findAll(pageRequest);

        return graduateEntities.map(graduate -> new GraduateDTO(
                graduate.getId(),
                graduate.getGraduateId(),
                graduate.getGraduatetitle(),
                graduate.getGraduateCreatedTime(),
                graduate.getGraduateLike(),
                graduate.getScrap()
        ));
    }

    @Transactional
    public List<MainGraduateDTO> getTop3FreeGraduates() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "graduateCreatedTime"));
        List<GraduateEntity> graduates = graduateRepository.findTop3GraduatesByGraduateId("Free", pageable);
        return graduates.stream()
                .map(MainGraduateDTO::toMainGraduateDTO)
                .collect(Collectors.toList());
    }

    /**
     * graduateId가 "Quest"인 상위 3개의 Graduate 게시글을 조회
     *
     * @return List<MainGraduateDTO> 상위 3개의 Graduate DTO 리스트
     */
    @Transactional
    public List<MainGraduateDTO> getTop3QuestGraduates() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "graduateCreatedTime"));
        List<GraduateEntity> graduates = graduateRepository.findTop3GraduatesByGraduateId("Quest", pageable);
        return graduates.stream()
                .map(MainGraduateDTO::toMainGraduateDTO)
                .collect(Collectors.toList());
    }
}

