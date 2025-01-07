package com.example.demo.service;


import com.example.demo.dto.*;
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
public class CodingService {
    private final CodingRepository codingRepository;
    private final CodingFileRepository codingFileRepository;
    private final CodingLikeRepository codingLikeRepository;
    private final CodingScrapRepository codingScrapRepository;


    public void save(CodingDTO codingDTO) throws IOException {
        // codingFile이 null이거나 비어 있는지 확인
        if (codingDTO.getCodingFile() == null || codingDTO.getCodingFile().isEmpty()) {
            CodingEntity codingEntity = CodingEntity.toSaveEntity(codingDTO);
            codingRepository.save(codingEntity);
        } else {
            CodingEntity codingEntity = CodingEntity.toSaveFileEntity(codingDTO);
            Long savedId = codingRepository.save(codingEntity).getId();
            CodingEntity board = codingRepository.findById(savedId).get();

            for (MultipartFile codingFile : codingDTO.getCodingFile()) {
                String originalFilename = codingFile.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + originalFilename;
                String savePath = "C:/springboot_img/" + storedFileName;

                // 파일을 지정된 경로에 저장
                codingFile.transferTo(new File(savePath));

                // CodingFileEntity 생성 및 저장
                CodingFileEntity codingFileEntity = CodingFileEntity.toCodingFileEntity(board, originalFilename, storedFileName);
                codingFileRepository.save(codingFileEntity);
            }
        }
    }




    @Transactional
    public CodingDTO findByID(Long id) {
        Optional<CodingEntity> optionalCodingEntity = codingRepository.findById(id);
        if(optionalCodingEntity.isPresent()) {
            CodingEntity codingEntity = optionalCodingEntity.get();
            CodingDTO codingDTO = CodingDTO.toCodingDTO(codingEntity);
            return codingDTO;
        }
        else {
            return null;
        }
    }

    @Transactional
    public CodingDTO findByID(Long id, String userId) {
        CodingEntity coding = codingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "퀘스트를 찾을 수 없습니다."));

        if (!coding.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "퀘스트에 대한 접근 권한이 없습니다.");
        }
        CodingDTO codingDTO = CodingDTO.toCodingDTO(coding);
        return codingDTO;
    }

    public CodingDTO update(CodingDTO codingDTO) {
        CodingEntity codingEntity = CodingEntity.toUpdatedEntity(codingDTO);
        codingRepository.save(codingEntity);
        return findByID(codingDTO.getId());
    }

    @Transactional
    public boolean delete(Long id, String userId) {
        Optional<CodingEntity> optionalCoding = codingRepository.findById(id);
        if (optionalCoding.isPresent()) {
            CodingEntity coding = optionalCoding.get();
            if (coding.getUserId().equals(userId)) {
                codingRepository.delete(coding);
                return true;
            }
        }
        return false;
    }

    public void deleteByAdmin(Long id) {
        codingRepository.deleteById(id);
    }

    public Page<CodingDTO> paging(String userId, Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0); // 페이지가 음수일 경우 0으로 설정
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수

        // pageable을 사용해 페이지와 정렬을 설정
        Page<CodingEntity> codingEntities = codingRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        // NoticeEntity를 NoticeDTO로 변환
        return codingEntities.map(coding -> new CodingDTO(
                coding.getId(),
                coding.getCodingtype(),
                coding.getCodingtitle(),
                coding.getCodingCreatedTime(),
                coding.getScrap(),
                codingScrapRepository.existsByUserIdAndCodingEntityId(userId, coding.getId())
        ));
    }


    @Transactional
    public Page<CodingDTO> searchByTitleOrContentOrHashtagOrType(String userId,String title, String content, String hashtag, String type, Pageable pageable) {
        Page<CodingEntity> codingEntities = codingRepository.findByTitleOrContentsContaining(title, content, hashtag, type, pageable);

        // Lazy-loaded 컬렉션을 초기화
        codingEntities.forEach(notice -> notice.getCodingFileEntityList().size());

        return codingEntities.map(coding -> new CodingDTO(
                coding.getId(),
                coding.getCodingtype(),
                coding.getCodingtitle(),
                coding.getCodingCreatedTime(),
                coding.getScrap(),
                codingScrapRepository.existsByUserIdAndCodingEntityId(userId, coding.getId())
        ));
    }

    @Transactional
    public void toggleLike(Long codingId, String userId) {
        CodingEntity coding = codingRepository.findById(codingId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + codingId));

        if (codingLikeRepository.existsByUserIdAndCodingEntityId(userId, codingId)) {
            // 좋아요가 이미 존재하면 삭제
            CodingLikeEntity like = codingLikeRepository.findByUserIdAndCodingEntityId(userId, codingId)
                    .orElseThrow(() -> new IllegalArgumentException("Like not found"));
            codingLikeRepository.delete(like);

            // 좋아요 수 감소
            if (coding.getCodingLike() > 0) {
                coding.setCodingLike(coding.getCodingLike() - 1);
                codingRepository.save(coding);
            }
        } else {
            // 좋아요가 없으면 추가
            CodingLikeEntity newLike = CodingLikeEntity.toCodingLikeEntity(coding, userId);
            codingLikeRepository.save(newLike);

            // 좋아요 수 증가
            coding.setCodingLike(coding.getCodingLike() + 1);
            codingRepository.save(coding);
        }
    }

    @Transactional
    public boolean hasUserLikedCoding(Long codingId, String userId) {
        return codingLikeRepository.existsByUserIdAndCodingEntityId(userId, codingId);
    }


    @Transactional
    public boolean toggleScrap(Long codingId, String userId) {

        CodingEntity coding = codingRepository.findById(codingId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + codingId));

        if (codingScrapRepository.existsByUserIdAndCodingEntityId(userId, codingId)) {
            // 스크랩이 이미 존재하면 삭제
            CodingScrapEntity scrap = codingScrapRepository.findByUserIdAndCodingEntityId(userId, codingId)
                    .orElseThrow(() -> new IllegalArgumentException("Scrap not found"));
            codingScrapRepository.delete(scrap);

            // 스크랩 수 감소
            if (coding.getScrap() > 0) {
                coding.setScrap(coding.getScrap() - 1);
                codingRepository.save(coding);
            }
            return false; // 스크랩 해제됨
        } else {
            // 스크랩이 없으면 추가
            CodingScrapEntity newScrap = CodingScrapEntity.toScrapEntity(coding, userId);
            codingScrapRepository.save(newScrap);

            // 스크랩 수 증가
            coding.setScrap(coding.getScrap() + 1);
            codingRepository.save(coding);
            return true; // 스크랩 추가됨
        }
    }

    @Transactional
    public boolean hasUserScrappedCoding(Long codingId, String userId) {
        return codingScrapRepository.existsByUserIdAndCodingEntityId(userId, codingId);
    }

    @Transactional
    public List<CodingDTO> getScrappedCoding(String userId, Long lastId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        List<CodingScrapEntity> scraps;

        if (lastId != null) {
            scraps = codingScrapRepository.findTopByUserIdAndIdLessThan(userId, lastId, pageRequest);
        } else {
            scraps = codingScrapRepository.findTopByUserIdAndIdLessThan(userId, Long.MAX_VALUE, pageRequest);
        }

        return scraps.stream()
                .map(scrap -> {
                    CodingEntity coding = scrap.getCodingEntity();
                    return new CodingDTO(
                            coding.getId(),
                            coding.getCodingtype(),
                            coding.getCodingtitle(),
                            coding.getCodingCreatedTime(),
                            coding.getScrap(),
                            codingScrapRepository.existsByUserIdAndCodingEntityId(userId, coding.getId())
                    );
                })
                .collect(Collectors.toList());
    }

    public List<CodingDTO> getTopLikedCodings(String userId) {
        int likeThreshold = 10;
        int limit = 3;
        PageRequest pageRequest = PageRequest.of(0, limit);

        List<CodingEntity> topLikedEntities = codingRepository.findByCodingLikeGreaterThanEqualOrderByCodingCreatedTimeDesc(likeThreshold, pageRequest);

        return topLikedEntities.stream()
                .map(coding -> new CodingDTO(
                        coding.getId(),
                        coding.getCodingtitle(),
                        coding.getCodingcontents(),
                        coding.getCodingCreatedTime(),
                        coding.getScrap(),
                        codingScrapRepository.existsByUserIdAndCodingEntityId(userId, coding.getId())
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public Page<CodingDTO> searchAndSortByLikes(String userId,String searchKeyword, String contentKeyword, String hashtagKeyword, String typeKeyword, Pageable pageable) {
        Page<CodingEntity> codingEntities = codingRepository.findByTitleOrContentsContaining(
                searchKeyword, contentKeyword, hashtagKeyword, typeKeyword, pageable
        );

        // Lazy-loaded 컬렉션 초기화 (필요 시)
        codingEntities.forEach(coding -> coding.getCodingFileEntityList().size());

        return codingEntities.map(coding -> new CodingDTO(
                coding.getId(),
                coding.getCodingtype(),
                coding.getCodingtitle(),
                coding.getCodingCreatedTime(),
                coding.getScrap(),
                codingScrapRepository.existsByUserIdAndCodingEntityId(userId, coding.getId())
        ));
    }

    @Transactional
    public Page<CodingDTO> sortByLikes(String userId,Pageable pageable) {
        Page<CodingEntity> codingEntities = codingRepository.findAll(pageable);

        // Lazy-loaded 컬렉션 초기화 (필요 시)
        codingEntities.forEach(coding -> coding.getCodingFileEntityList().size());

        return codingEntities.map(coding -> new CodingDTO(
                coding.getId(),
                coding.getCodingtype(),
                coding.getCodingtitle(),
                coding.getCodingCreatedTime(),
                coding.getScrap(),
                codingScrapRepository.existsByUserIdAndCodingEntityId(userId, coding.getId())
        ));
    }


    @Transactional
    public List<MainCodingDTO> getTop2Codings() {
        PageRequest pageable = PageRequest.of(0, 2);
        List<CodingEntity> codings = codingRepository.findTop2CodingsWithFiles(pageable);
        return codings.stream()
                .map(MainCodingDTO::toMainCodingDTO)
                .collect(Collectors.toList());
    }

    // 기존 코드에 추가
    @Transactional
    public List<CodingDTO> findAllByUserId(String userId) {
        List<CodingEntity> codingEntities = codingRepository.findByUserId(userId);
        return codingEntities.stream()
                .map(CodingDTO::toCodingDTO)
                .collect(Collectors.toList());
    }


}
