package com.example.demo.service;

import com.example.demo.dto.FreeDTO;
import com.example.demo.dto.GraduateDTO;
import com.example.demo.entity.FreeEntity;
import com.example.demo.entity.FreeFileEntity;
import com.example.demo.entity.GraduateEntity;
import com.example.demo.repository.GraduateRepository;
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
    public void increaseLike(Long id) {
        GraduateEntity graduate = graduateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coding not found with id: " + id));
        graduate.setGraduateLike(graduate.getGraduateLike() + 1);
        graduateRepository.save(graduate);
    }


    @Transactional
    public void toggleScrap(Long id) {
        GraduateEntity graduate = graduateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coding not found with id: " + id));
        graduate.setScrap(graduate.getScrap() == 1 ? 0 : 1);
        graduateRepository.save(graduate);
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
}

