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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyService {
    private final StudyRepository studyRepository;
    private final StudyFileRepository studyFileRepository;
    private final ApplyRepository applyRepository;
    private final StudyLikeRepository studyLikeRepository;
    private final StudyScrapRepository studyScrapRepository;


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

    public Page<StudyDTO> paging(String userId,Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0); // 페이지가 음수일 경우 0으로 설정
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수

        // pageable을 사용해 페이지와 정렬을 설정
        Page<StudyEntity> studyEntities = studyRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        // 현재 날짜
        LocalDate today = LocalDate.now();

        // 엔티티를 DTO로 변환하면서 daysLeft 계산
        Page<StudyDTO> studyDTOPage = studyEntities.map(study -> {
            long daysLeft = ChronoUnit.DAYS.between(today, study.getDeadline().toLocalDate());

            return new StudyDTO(
                    study.getId(),
                    study.getStudyId(),
                    study.getStudytitle(),
                    study.getStartTime(),
                    study.getDeadline(),
                    study.getRecruit(),
                    study.getCountMember(),
                    study.getScrap(),
                    daysLeft,
                    studyScrapRepository.existsByUserIdAndStudyEntityId(userId, study.getId())
            );
        });

        return studyDTOPage;
    }


    @Transactional
    public Page<StudyDTO> searchByTitleOrContentOrHashtagOrType(String userId,String studyid, String title, String content, String hashtag, Pageable pageable) {
        Page<StudyEntity> studyEntities = studyRepository.findByTitleOrContentsContaining(studyid, title, content, hashtag, pageable);

        // Lazy-loaded 컬렉션을 초기화
        studyEntities.forEach(study -> study.getStudyFileEntityList().size());

        LocalDate today = LocalDate.now();
        // 엔티티를 DTO로 변환하면서 daysLeft 계산
        Page<StudyDTO> studyDTOPage = studyEntities.map(study -> {
            long daysLeft = ChronoUnit.DAYS.between(today, study.getDeadline().toLocalDate());

            return new StudyDTO(
                    study.getId(),
                    study.getStudyId(),
                    study.getStudytitle(),
                    study.getStartTime(),
                    study.getDeadline(),
                    study.getRecruit(),
                    study.getCountMember(),
                    study.getScrap(),
                    daysLeft,
                    studyScrapRepository.existsByUserIdAndStudyEntityId(userId, study.getId())
            );
        });

        return studyDTOPage;
    }

    //마감임박순
    @Transactional
    public Page<StudyDTO> searchdeadline(String userId,String studyid, String title, String content, String hashtag, Pageable pageable) {
        Page<StudyEntity> studyEntities = studyRepository.searchStudiesByFilters(studyid, title, content, hashtag, pageable);

        // Lazy-loaded 컬렉션을 초기화
        studyEntities.forEach(study -> study.getStudyFileEntityList().size());

        LocalDate today = LocalDate.now();
        // 엔티티를 DTO로 변환하면서 daysLeft 계산
        Page<StudyDTO> studyDTOPage = studyEntities.map(study -> {
            long daysLeft = ChronoUnit.DAYS.between(today, study.getDeadline().toLocalDate());

            return new StudyDTO(
                    study.getId(),
                    study.getStudyId(),
                    study.getStudytitle(),
                    study.getStartTime(),
                    study.getDeadline(),
                    study.getRecruit(),
                    study.getCountMember(),
                    study.getScrap(),
                    daysLeft,
                    studyScrapRepository.existsByUserIdAndStudyEntityId(userId, study.getId())
            );
        });

        return studyDTOPage;
    }


    @Transactional
    public Page<StudyDTO> sortBydeadline(String userId,Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0); // 페이지가 음수일 경우 0으로 설정
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수

        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        // pageable을 사용해 페이지와 정렬을 설정 (마감 임박순: deadline 오름차순)
        Pageable pageRequest = PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.ASC, "deadline")); // 필드 이름 수정: "dealine" → "deadline"

        // 마감일이 현재 시간 이후인 스터디만 조회
        Page<StudyEntity> studyEntities = studyRepository.findByDeadlineGreaterThanEqualOrderByDeadlineAsc(now, pageRequest);

        // 엔티티를 DTO로 변환하면서 daysLeft 계산
        return studyEntities.map(study -> {
            long daysLeft = 0;
            if (study.getDeadline() != null) {
                LocalDate today = LocalDate.now();
                LocalDate deadlineDate = study.getDeadline().toLocalDate();
                daysLeft = ChronoUnit.DAYS.between(today, deadlineDate);
                daysLeft = daysLeft >= 0 ? daysLeft : 0; // 음수일 경우 0으로 설정
            }

            return new StudyDTO(
                    study.getId(),
                    study.getStudyId(),
                    study.getStudytitle(),
                    study.getStartTime(),
                    study.getDeadline(),
                    study.getRecruit(),
                    study.getCountMember(),
                    study.getScrap(),
                    daysLeft,
                    studyScrapRepository.existsByUserIdAndStudyEntityId(userId, study.getId())
            );
        });
    }


    @Transactional
    public void toggleLike(Long studyId, String userId) {
        StudyEntity study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + studyId));

        if (studyLikeRepository.existsByUserIdAndStudyEntityId(userId, studyId)) {
            // 좋아요가 이미 존재하면 삭제
            StudyLikeEntity like = studyLikeRepository.findByUserIdAndStudyEntityId(userId, studyId)
                    .orElseThrow(() -> new IllegalArgumentException("Like not found"));
            studyLikeRepository.delete(like);

            // 좋아요 수 감소
            if (study.getStudyLike() > 0) {
                study.setStudyLike(study.getStudyLike() - 1);
                studyRepository.save(study);
            }
        } else {
            // 좋아요가 없으면 추가
            StudyLikeEntity newLike = StudyLikeEntity.toStudyLikeEntity(study, userId);
            studyLikeRepository.save(newLike);

            // 좋아요 수 증가
            study.setStudyLike(study.getStudyLike() + 1);
            studyRepository.save(study);
        }
    }

    @Transactional
    public boolean hasUserLikedStudy(Long studyId, String userId) {
        return studyLikeRepository.existsByUserIdAndStudyEntityId(userId, studyId);
    }


    @Transactional
    public boolean toggleScrap(Long studyId, String userId) {
        StudyEntity study = studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + studyId));

        if (studyScrapRepository.existsByUserIdAndStudyEntityId(userId, studyId)) {
            // 스크랩이 이미 존재하면 삭제
            StudyScrapEntity scrap = studyScrapRepository.findByUserIdAndStudyEntityId(userId, studyId)
                    .orElseThrow(() -> new IllegalArgumentException("Scrap not found"));
            studyScrapRepository.delete(scrap);

            // 스크랩 수 감소
            if (study.getScrap() > 0) {
                study.setScrap(study.getScrap() - 1);
                studyRepository.save(study);
            }
            return false; // 스크랩 해제됨
        } else {
            // 스크랩이 없으면 추가
            StudyScrapEntity newScrap = StudyScrapEntity.toScrapEntity(study, userId);
            studyScrapRepository.save(newScrap);

            // 스크랩 수 증가
            study.setScrap(study.getScrap() + 1);
            studyRepository.save(study);
            return true; // 스크랩 추가됨
        }
    }

    @Transactional
    public boolean hasUserScrappedStudy(Long questId, String userId) {
        return studyScrapRepository.existsByUserIdAndStudyEntityId(userId, questId);
    }


    @Transactional
    public StudyResponse getScrappedQuests(String userId, Long lastStudyId, String studyId, int limit) {
        // 페이징 설정: limit + 1 조회하여 hasMore 판단
        Pageable pageable = PageRequest.of(0, limit + 1);

        List<StudyScrapEntity> scraps;
        if (lastStudyId != null) {
            scraps = studyScrapRepository.findByUserIdAndStudyEntityIdLessThanOrderByStudyEntityIdDesc(userId, lastStudyId, studyId, pageable);
        } else {
            scraps = studyScrapRepository.findByUserIdOrderByStudyEntityIdDesc(userId, studyId, pageable);
        }

        if (scraps.isEmpty()) {
            return new StudyResponse(Collections.emptyList(), false);
        }

        // 스터디 엔티티 ID 추출
        List<Long> studyIds = scraps.stream()
                .map(scrap -> scrap.getStudyEntity().getId())
                .collect(Collectors.toList());

        // 스터디 엔티티 조회
        List<StudyEntity> studies = studyRepository.findAllByIdIn(studyIds);

        // 스터디 ID를 키로 하는 맵 생성 (빠른 조회를 위해)
        Map<Long, StudyEntity> studyMap = studies.stream()
                .collect(Collectors.toMap(StudyEntity::getId, Function.identity()));

        LocalDate today = LocalDate.now();

        // StudyDTO로 변환하면서 daysLeft 계산
        List<StudyDTO> studyDTOs = scraps.stream()
                .map(scrap -> {
                    StudyEntity study = studyMap.get(scrap.getStudyEntity().getId());
                    if (study == null) {
                        return null; // 해당 스터디가 없는 경우 null로 설정
                    }
                    long daysLeft = ChronoUnit.DAYS.between(today, study.getDeadline().toLocalDate());
                    return new StudyDTO(
                            study.getId(),
                            study.getStudyId(),
                            study.getStudytitle(),
                            study.getStartTime(),
                            study.getDeadline(),
                            study.getRecruit(),
                            study.getCountMember(),
                            study.getScrap(),
                            daysLeft,
                            studyScrapRepository.existsByUserIdAndStudyEntityId(userId, study.getId())
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        boolean hasMore = false;
        if (studyDTOs.size() > limit) {
            hasMore = true;
            studyDTOs.remove(studyDTOs.size() - 1); // limit을 초과한 마지막 항목 제거
        }

        return new StudyResponse(studyDTOs, hasMore);
    }

    public List<StudyDTO> getTopLikedFrees(String userId) {
        int likeThreshold = 10;
        int limit = 3;
        Pageable pageRequest = PageRequest.of(0, limit);

        List<StudyEntity> topLikedEntities = studyRepository.findByStudyLikeGreaterThanEqualOrderByStudyCreatedTimeDesc(likeThreshold, pageRequest);
        LocalDate today = LocalDate.now();

        return topLikedEntities.stream()
                .map(study -> {
                    // daysLeft 계산
                    long daysLeft = ChronoUnit.DAYS.between(today, study.getDeadline().toLocalDate());

                    // StudyDTO 생성자에 daysLeft 포함
                    return new StudyDTO(
                            study.getId(),
                            study.getStudyId(),
                            study.getStudytitle(),
                            study.getStartTime(),
                            study.getDeadline(),
                            study.getRecruit(),
                            study.getCountMember(),
                            study.getScrap(),
                            daysLeft,
                            studyScrapRepository.existsByUserIdAndStudyEntityId(userId, study.getId())
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, List<MainStudyDTO>> getTop3StudiesByCategories() {
        Map<String, List<MainStudyDTO>> topStudiesMap = new HashMap<>();
        String[] categories = {"bootcamp", "industry", "study"};

        for (String category : categories) {
            List<StudyEntity> studies = studyRepository.findTopStudiesByStudyId(category, PageRequest.of(0, 1));
            List<MainStudyDTO> studyDTOs = new ArrayList<>();

            for (StudyEntity studyEntity : studies) {
                studyDTOs.add(MainStudyDTO.toMainStudyDTO(studyEntity));
            }

            topStudiesMap.put(category, studyDTOs);
        }

        return topStudiesMap;
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

    // 기존 코드에 추가
    @Transactional
    public List<StudyDTO> findAllByUserId(String userId) {
        List<StudyEntity> studyEntities = studyRepository.findByUserId(userId);
        return studyEntities.stream()
                .map(StudyDTO::toStudyDTO)
                .collect(Collectors.toList());
    }


}
