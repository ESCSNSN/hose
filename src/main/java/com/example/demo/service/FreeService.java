package com.example.demo.service;

import com.example.demo.dto.FreeDTO;
import com.example.demo.dto.MainFreeDTO;
import com.example.demo.dto.QuestDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
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
public class FreeService {
    private final FreeRepository freeRepository;
    private final FreeFileRepository freeFileRepository;
    private final FreeLikeRepository freeLikeRepository;
    private final FreeScrapRepository freeScrapRepository;



    public void deleteByAdmin(Long id) {
        freeRepository.deleteById(id);
    }

    public void save(FreeDTO freeDTO) throws IOException {
        // codingFile이 null이거나 비어 있는지 확인
        if (freeDTO.getFreeFile() == null || freeDTO.getFreeFile().isEmpty()) {
            FreeEntity freeEntity = FreeEntity.toSaveEntity(freeDTO);
            freeRepository.save(freeEntity);
        } else {
            FreeEntity freeEntity = FreeEntity.toSaveFileEntity(freeDTO);
            Long savedId = freeRepository.save(freeEntity).getId();
            FreeEntity board = freeRepository.findById(savedId).get();

            for (MultipartFile freeFile : freeDTO.getFreeFile()) {
                String originalFilename = freeFile.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + originalFilename;
                String savePath = "C:/springboot_img/" + storedFileName;

                // 파일을 지정된 경로에 저장
                freeFile.transferTo(new File(savePath));

                // CodingFileEntity 생성 및 저장
                FreeFileEntity freeFileEntity = FreeFileEntity.toFreeFileEntity(board, originalFilename, storedFileName);
                freeFileRepository.save(freeFileEntity);
            }
        }
    }




    @Transactional
    public FreeDTO findByID(Long id) {
        Optional<FreeEntity> optionalFreeEntity = freeRepository.findById(id);
        if(optionalFreeEntity.isPresent()) {
            FreeEntity freeEntity = optionalFreeEntity.get();
            FreeDTO freeDTO = FreeDTO.toFreeDTO(freeEntity);
            return freeDTO;
        }
        else {
            return null;
        }
    }


    @Transactional
    public FreeDTO findByID(Long id, String userId) {
        FreeEntity free = freeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "퀘스트를 찾을 수 없습니다."));

        if (!free.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "퀘스트에 대한 접근 권한이 없습니다.");
        }
        FreeDTO freeDTO = FreeDTO.toFreeDTO(free);
        return freeDTO;
    }

    public FreeDTO update(FreeDTO freeDTO) {
        FreeEntity freeEntity = FreeEntity.toUpdatedEntity(freeDTO);
        freeRepository.save(freeEntity);
        return findByID(freeDTO.getId());
    }

    @Transactional
    public boolean delete(Long id, String userId) {
        Optional<FreeEntity> optionalQuest = freeRepository.findById(id);
        if (optionalQuest.isPresent()) {
            FreeEntity free = optionalQuest.get();
            if (free.getUserId().equals(userId)) {
                freeRepository.delete(free);
                return true;
            }
        }
        return false;
    }

    public Page<FreeDTO> paging(Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0); // 페이지가 음수일 경우 0으로 설정
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수

        // pageable을 사용해 페이지와 정렬을 설정
        Page<FreeEntity> freeEntities = freeRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));


        return freeEntities.map(free -> new FreeDTO(
                free.getId(),
                free.getFreetitle(),
                free.getFreeCreatedTime(),
                free.getFreeLike(),
                free.getScrap()
        ));
    }


    @Transactional
    public Page<FreeDTO> searchByTitleOrContentOrHashtagOrType(String title, String content, String hashtag,  Pageable pageable) {
        Page<FreeEntity> freeEntities = freeRepository.findByTitleOrContentsContaining(title, content, hashtag,  pageable);

        // Lazy-loaded 컬렉션을 초기화
        freeEntities.forEach(notice -> notice.getFreeFileEntityList().size());

        return freeEntities.map(FreeDTO::toFreeDTO);
    }

    @Transactional
    public void toggleLike(Long freeId, String userId) {
        FreeEntity free = freeRepository.findById(freeId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + freeId));

        if (freeLikeRepository.existsByUserIdAndFreeEntityId(userId, freeId)) {
            // 좋아요가 이미 존재하면 삭제
            FreeLikeEntity like = freeLikeRepository.findByUserIdAndFreeEntityId(userId, freeId)
                    .orElseThrow(() -> new IllegalArgumentException("Like not found"));
            freeLikeRepository.delete(like);

            // 좋아요 수 감소
            if (free.getFreeLike() > 0) {
                free.setFreeLike(free.getFreeLike() - 1);
                freeRepository.save(free);
            }
        } else {
            // 좋아요가 없으면 추가
            FreeLikeEntity newLike = FreeLikeEntity.toFreeLikeEntity(free, userId);
            freeLikeRepository.save(newLike);

            // 좋아요 수 증가
            free.setFreeLike(free.getFreeLike() + 1);
            freeRepository.save(free);
        }
    }

    @Transactional
    public boolean hasUserLikedFree(Long freeId, String userId) {
        return freeLikeRepository.existsByUserIdAndFreeEntityId(userId, freeId);
    }


    @Transactional
    public boolean toggleScrap(Long freeId, String userId) {

        FreeEntity free = freeRepository.findById(freeId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + freeId));

        if (freeScrapRepository.existsByUserIdAndFreeEntityId(userId, freeId)) {
            // 스크랩이 이미 존재하면 삭제
            FreeScrapEntity scrap = freeScrapRepository.findByUserIdAndFreeEntityId(userId, freeId)
                    .orElseThrow(() -> new IllegalArgumentException("Scrap not found"));
            freeScrapRepository.delete(scrap);

            // 스크랩 수 감소
            if (free.getScrap() > 0) {
                free.setScrap(free.getScrap() - 1);
                freeRepository.save(free);
            }
            return false; // 스크랩 해제됨
        } else {
            // 스크랩이 없으면 추가
            FreeScrapEntity newScrap = FreeScrapEntity.toScrapEntity(free, userId);
            freeScrapRepository.save(newScrap);

            // 스크랩 수 증가
            free.setScrap(free.getScrap() + 1);
            freeRepository.save(free);
            return true; // 스크랩 추가됨
        }
    }

    @Transactional
    public boolean hasUserScrappedFree(Long questId, String userId) {
        return freeScrapRepository.existsByUserIdAndFreeEntityId(userId, questId);
    }

    @Transactional
    public List<FreeDTO> getScrappedFree(String userId, Long lastId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        List<FreeScrapEntity> scraps;

        if (lastId != null) {
            scraps = freeScrapRepository.findTopByUserIdAndIdLessThan(userId, lastId, pageRequest);
        } else {
            scraps = freeScrapRepository.findTopByUserIdAndIdLessThan(userId, Long.MAX_VALUE, pageRequest);
        }

        return scraps.stream()
                .map(scrap -> {
                    FreeEntity free = scrap.getFreeEntity();
                    return new FreeDTO(
                            free.getId(),
                            free.getFreetitle(),
                            free.getFreeCreatedTime(),
                            free.getFreeLike(),
                            free.getScrap()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<FreeDTO> getTopLikedFrees() {
        int likeThreshold = 10;
        int limit = 3;
        PageRequest pageRequest = PageRequest.of(0, limit);

        List<FreeEntity> topLikedEntities = freeRepository.findByFreeLikeGreaterThanEqualOrderByFreeCreatedTimeDesc(likeThreshold, pageRequest);

        return topLikedEntities.stream()
                .map(free -> new FreeDTO(
                        free.getId(),
                        free.getFreetitle(),
                        free.getFreeCreatedTime(),
                        free.getFreeLike(),
                        free.getScrap()
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public Page<FreeDTO> searchAndSortByLikes(String searchKeyword, String contentKeyword, String hashtagKeyword, Pageable pageable) {
        Page<FreeEntity> freeEntities = freeRepository.findByTitleOrContentsContaining(
                searchKeyword, contentKeyword, hashtagKeyword, pageable);

        // Lazy-loaded 컬렉션 초기화 (필요 시)
        freeEntities.forEach(free -> free.getFreeFileEntityList().size());

        return freeEntities.map(FreeDTO::toFreeDTO);
    }


    @Transactional
    public Page<FreeDTO> sortByLikes(Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0); // 페이지가 음수일 경우 0으로 설정
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수

        Pageable pageRequest = PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "freeLike")); // 필드 이름 확인

        Page<FreeEntity> freeEntities = freeRepository.findAll(pageRequest);

        return freeEntities.map(free -> new FreeDTO(
                free.getId(),
                free.getFreetitle(),
                free.getFreeCreatedTime(),
                free.getFreeLike(),
                free.getScrap()
        ));
    }

    @Transactional
    public List<MainFreeDTO> getTop3FreePosts() {
        PageRequest pageable = PageRequest.of(0, 3);
        List<FreeEntity> freePosts = freeRepository.findTop3FreePostsWithFiles(pageable);
        return freePosts.stream()
                .map(MainFreeDTO::toMainFreeDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<MainFreeDTO> getTop2FreePosts() {
        PageRequest pageable = PageRequest.of(0, 2);
        List<FreeEntity> freePosts = freeRepository.findTop3FreePostsWithFiles(pageable);
        return freePosts.stream()
                .map(MainFreeDTO::toMainFreeDTO)
                .collect(Collectors.toList());
    }

    // 기존 코드에 추가
    @Transactional
    public List<FreeDTO> findAllByUserId(String userId) {
        List<FreeEntity> freeEntities = freeRepository.findByUserId(userId);
        return freeEntities.stream()
                .map(FreeDTO::toFreeDTO)
                .collect(Collectors.toList());
    }


}
