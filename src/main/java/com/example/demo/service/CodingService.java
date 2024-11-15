package com.example.demo.service;


import com.example.demo.dto.CodingDTO;
import com.example.demo.dto.CompetitionDTO;
import com.example.demo.entity.CodingEntity;
import com.example.demo.entity.CodingFileEntity;
import com.example.demo.entity.CompetitionEntity;
import com.example.demo.entity.CompetitionFileEntity;
import com.example.demo.repository.CodingFileRepository;
import com.example.demo.repository.CodingRepository;
import com.example.demo.repository.CompetitionFileRepository;
import com.example.demo.repository.CompetitionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public CodingDTO update(CodingDTO codingDTO) {
        CodingEntity codingEntity = CodingEntity.toUpdatedEntity(codingDTO);
        codingRepository.save(codingEntity);
        return findByID(codingDTO.getId());
    }

    public void delete(Long id) {
        codingRepository.deleteById(id);
    }

    public Page<CodingDTO> paging(Pageable pageable) {
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
                coding.getScrap()
        ));
    }


    @Transactional
    public Page<CodingDTO> searchByTitleOrContentOrHashtagOrType(String title, String content, String hashtag, String type, Pageable pageable) {
        Page<CodingEntity> codingEntities = codingRepository.findByTitleOrContentsContaining(title, content, hashtag, type, pageable);

        // Lazy-loaded 컬렉션을 초기화
        codingEntities.forEach(notice -> notice.getCodingFileEntityList().size());

        return codingEntities.map(CodingDTO::toCodingDTO);
    }

    @Transactional
    public void increaseLike(Long id) {
        CodingEntity coding = codingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coding not found with id: " + id));
        coding.setCodingLike(coding.getCodingLike() + 1);
        codingRepository.save(coding);
    }


    @Transactional
    public void toggleScrap(Long id) {
        CodingEntity coding = codingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coding not found with id: " + id));
        coding.setScrap(coding.getScrap() == 1 ? 0 : 1);
        codingRepository.save(coding);
    }

    public List<CodingDTO> getTopLikedCodings() {
        int likeThreshold = 10;
        int limit = 3;
        PageRequest pageRequest = PageRequest.of(0, limit);

        List<CodingEntity> topLikedEntities = codingRepository.findBycodingLikeGreaterThanEqualOrderByCodingCreatedTimeDesc(likeThreshold, pageRequest);

        return topLikedEntities.stream()
                .map(coding -> new CodingDTO(
                        coding.getId(),
                        coding.getCodingtitle(),
                        coding.getCodingcontents(),
                        coding.getCodingCreatedTime(),
                        coding.getScrap()
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public Page<CodingDTO> searchAndSortByLikes(String searchKeyword, String contentKeyword, String hashtagKeyword, String typeKeyword, Pageable pageable) {
        Page<CodingEntity> codingEntities = codingRepository.findByTitleOrContentsContaining(
                searchKeyword, contentKeyword, hashtagKeyword, typeKeyword, pageable
        );

        // Lazy-loaded 컬렉션 초기화 (필요 시)
        codingEntities.forEach(coding -> coding.getCodingFileEntityList().size());

        return codingEntities.map(CodingDTO::toCodingDTO);
    }

    @Transactional
    public Page<CodingDTO> sortByLikes(Pageable pageable) {
        Page<CodingEntity> codingEntities = codingRepository.findAll(pageable);

        // Lazy-loaded 컬렉션 초기화 (필요 시)
        codingEntities.forEach(coding -> coding.getCodingFileEntityList().size());

        return codingEntities.map(CodingDTO::toCodingDTO);
    }

}
