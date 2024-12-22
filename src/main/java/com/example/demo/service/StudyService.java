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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final StudyFileRepository studyFileRepository;
    private final ApplyRepository applyRepository;


    public void save(StudyDTO studyDTO) throws IOException {
        // codingFile이 null이거나 비어 있는지 확인
        if (studyDTO.getStudyFile() == null || studyDTO.getStudyFile().isEmpty()) {
            StudyEntity studyEntity = StudyEntity.toSaveEntity(studyDTO);
            studyRepository.save(studyEntity);
        } else {
            StudyEntity studyEntity = StudyEntity.toSaveFileEntity(studyDTO);
            Long savedId = studyRepository.save(studyEntity).getId();
            StudyEntity board = studyRepository.findById(savedId).get();

            for (MultipartFile studyFile : studyDTO.getStudyFile()) {
                String originalFilename = studyFile.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + originalFilename;
                String savePath = "C:/springboot_img/" + storedFileName;

                // 파일을 지정된 경로에 저장
                studyFile.transferTo(new File(savePath));

                // CodingFileEntity 생성 및 저장
                StudyFileEntity studyFileEntity = StudyFileEntity.toStudyFileEntity(board, originalFilename, storedFileName);
                studyFileRepository.save(studyFileEntity);
            }
        }
    }


    @Transactional
    public StudyDTO findByID(Long id) {
        Optional<StudyEntity> optionalStudyEntity = studyRepository.findById(id);
        if (optionalStudyEntity.isPresent()) {
            StudyEntity studyEntity = optionalStudyEntity.get();
            StudyDTO studyDTO = StudyDTO.toStudyDTO(studyEntity);
            return studyDTO;
        } else {
            return null;
        }
    }

    @Transactional
    public StudyDTO findByID(Long id, String userId) {
        StudyEntity study = studyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "퀘스트를 찾을 수 없습니다."));

        if (!study.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "퀘스트에 대한 접근 권한이 없습니다.");
        }
        StudyDTO studyDTO = StudyDTO.toStudyDTO(study);
        return studyDTO;
    }

    public StudyDTO update(StudyDTO studyDTO) {
        StudyEntity studyEntity = StudyEntity.toUpdatedEntity(studyDTO);
        studyRepository.save(studyEntity);
        return findByID(studyDTO.getId());
    }

    @Transactional
    public boolean delete(Long id, String userId) {
        Optional<StudyEntity> optionalStudy = studyRepository.findById(id);
        if (optionalStudy.isPresent()) {
            StudyEntity study = optionalStudy.get();
            if (study.getUserId().equals(userId)) {
                studyRepository.delete(study);
                return true;
            }
        }
        return false;
    }

    public Page<StudyDTO> paging(Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0); // 페이지가 음수일 경우 0으로 설정
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수

        // pageable을 사용해 페이지와 정렬을 설정
        Page<StudyEntity> studyEntities = studyRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));


        return studyEntities.map(study -> new StudyDTO(
                study.getId(),
                study.getStudyId(),
                study.getStudytitle(),
                study.getDeadline(),
                study.getRecruit(),
                study.getCountMember(),
                study.getScrap()
        ));
    }


    @Transactional
    public Page<StudyDTO> searchByTitleOrContentOrHashtagOrType(String studyid, String title, String content, String hashtag, Pageable pageable) {
        Page<StudyEntity> studyEntities = studyRepository.findByTitleOrContentsContaining(studyid, title, content, hashtag, pageable);

        // Lazy-loaded 컬렉션을 초기화
        studyEntities.forEach(study -> study.getStudyFileEntityList().size());

        return studyEntities.map(StudyDTO::toStudyDTO);
    }

    //마감임박순
    @Transactional
    public Page<StudyDTO> searchdeadline(String studyid, String title, String content, String hashtag, Pageable pageable) {
        Page<StudyEntity> studyEntities = studyRepository.searchStudiesByFilters(studyid, title, content, hashtag, pageable);

        // Lazy-loaded 컬렉션을 초기화
        studyEntities.forEach(study -> study.getStudyFileEntityList().size());

        return studyEntities.map(StudyDTO::toStudyDTO);
    }

    @Transactional
    public Page<StudyDTO> sortBydeadline(Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0); // 페이지가 음수일 경우 0으로 설정
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수

        Pageable pageRequest = PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "dealine")); // 필드 이름 확인

        Page<StudyEntity> studyEntities = studyRepository.findAll(pageRequest);

        return studyEntities.map(study -> new StudyDTO(
                study.getId(),
                study.getStudyId(),
                study.getStudytitle(),
                study.getDeadline(),
                study.getRecruit(),
                study.getCountMember(),
                study.getScrap()
        ));
    }

    @Transactional
    public void increaseLike(Long id) {
        StudyEntity study = studyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coding not found with id: " + id));
        study.setStudyLike(study.getStudyLike() + 1);
        studyRepository.save(study);
    }


    @Transactional
    public void toggleScrap(Long id) {
        StudyEntity study = studyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coding not found with id: " + id));
        study.setScrap(study.getScrap() == 1 ? 0 : 1);
        studyRepository.save(study);
    }

    public List<StudyDTO> getTopLikedFrees() {
        int likeThreshold = 10;
        int limit = 3;
        PageRequest pageRequest = PageRequest.of(0, limit);

        List<StudyEntity> topLikedEntities = studyRepository.findByStudyLikeGreaterThanEqualOrderByStudyCreatedTimeDesc(likeThreshold, pageRequest);

        return topLikedEntities.stream()
                .map(study -> new StudyDTO(
                        study.getId(),
                        study.getStudyId(),
                        study.getStudytitle(),
                        study.getDeadline(),
                        study.getRecruit(),
                        study.getCountMember(),
                        study.getScrap()
                ))
                .collect(Collectors.toList());
    }


    /**
     * 지원 신청을 저장하는 메서드
     *
     * @param studyId  신청할 스터디의 ID
     * @param applyDTO 신청자의 정보가 담긴 DTO
     * @return 저장된 ApplyEntity
     */
    @Transactional
    public ApplyEntity applyToStudy(Long studyId, ApplyRequestDTO applyDTO) {
        StudyEntity study = studyRepository.findById(studyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "스터디를 찾을 수 없습니다."));

        String applyUserId = applyDTO.getApplyUserId();

        // 중복 신청 방지 로직
        boolean alreadyApplied = applyRepository.existsByApplyUserIdAndStudyEntityId(applyUserId, studyId);
        if (alreadyApplied) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 해당 스터디에 신청하였습니다.");
        }

        ApplyEntity applyEntity = ApplyEntity.toApplyEntity(study, applyUserId, false);
        return applyRepository.save(applyEntity);
    }

    /**
     * 지원 신청을 수락하는 메서드
     *
     * @param userId  헤더에서 추출한 사용자 ID
     * @param applyId 지원 신청의 ID
     */
    @Transactional
    public void acceptApplication(String userId, Long applyId) {
        ApplyEntity applyEntity = applyRepository.findById(applyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "지원 신청을 찾을 수 없습니다."));

        StudyEntity study = applyEntity.getStudyEntity();

        // study의 userId와 요청한 userId가 일치하는지 확인
        if (!study.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        if (applyEntity.isAccept()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 수락된 신청입니다.");
        }

        if (study.getCountMember() >= study.getRecruit()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "모집 인원이 이미 가득 찼습니다.");
        }

        // 신청 수락 처리
        applyEntity.setAccept(true);
        applyRepository.save(applyEntity);

        // 스터디의 회원 수 증가
        study.setCountMember(study.getCountMember() + 1);
        studyRepository.save(study);
    }

    /**
     * 스터디에 대한 지원자 목록을 조회하는 메서드
     *
     * @param userId  요청한 사용자 ID (스터디 소유자)
     * @param studyId 조회할 스터디의 ID
     * @return 지원자 목록을 포함한 응답 DTO
     */
    @Transactional
    public ApplicantListResponseDTO getApplicants(String userId, Long studyId) {
        StudyEntity study = studyRepository.findById(studyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "스터디를 찾을 수 없습니다."));

        // 스터디 소유자인지 확인
        if (!study.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        List<ApplyEntity> applicants = applyRepository.findByStudyEntityId(studyId);

        List<ApplicantResponseDTO> applicantDTOs = applicants.stream()
                .map(apply -> new ApplicantResponseDTO(
                        apply.getId(),
                        apply.getApplyUserId(),
                        apply.isAccept(),
                        studyId
                ))
                .collect(Collectors.toList());

        return new ApplicantListResponseDTO(studyId, study.getStudytitle(),applicantDTOs);
    }

    /**
     * 지원 신청을 거절하는 메서드 (apply_table에서 삭제)
     *
     * @param userId  헤더에서 추출한 사용자 ID
     * @param applyId 지원 신청의 ID
     */
    @Transactional
    public void rejectApplication(String userId, Long applyId) {
        ApplyEntity applyEntity = applyRepository.findById(applyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "지원 신청을 찾을 수 없습니다."));

        StudyEntity study = applyEntity.getStudyEntity();

        // study의 userId와 요청한 userId가 일치하는지 확인
        if (!study.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        // 지원 거절(삭제)
        applyRepository.delete(applyEntity);
    }

}
