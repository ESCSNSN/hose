package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.ApplyEntity;
import com.example.demo.service.StudyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/board")
public class StudiesController {

    private final StudyService studyService;

    public StudiesController(StudyService studyService) {
        this.studyService = studyService;
    }


    // GET /api/board/coding
    @GetMapping("/studies")
    public Page<StudyDTO> paging(@RequestParam(value = "page", required = false) Integer page,
                                 @RequestParam(value = "size", defaultValue = "10") Integer size,
                                 @RequestParam(value = "studyid", required = false) String studyid,
                                 @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                                 @RequestParam(value = "contentKeyword", required = false) String contentKeyword,
                                 @RequestParam(value = "hashtagKeyword", required = false) String hashtagKeyword) {

        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<StudyDTO> studyList;

        if (    (studyid == null || studyid.isEmpty()) &&
                (searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty())) {
            studyList = studyService.paging(pageable);
        } else {
            studyList = studyService.searchByTitleOrContentOrHashtagOrType(studyid,searchKeyword, contentKeyword, hashtagKeyword, pageable);
        }

        return studyList;
    }

    // GET /api/board/coding/save
    @GetMapping("/studies/save")
    public StudyDTO saveForm() {
        return new StudyDTO(); // 기본 구조의 CodingDTO 반환
    }

    // POST /api/board/quest/save
    @PostMapping(value = "/studies/save", consumes = {"multipart/form-data"})
    public ResponseEntity<StudyDTO> save(@ModelAttribute StudyDTO studyDTO) throws IOException {
        studyService.save(studyDTO);
        return ResponseEntity.ok(studyDTO); // 200 OK
    }

    // GET /api/board/coding/{id}
    @GetMapping("/studies/{id}")
    public StudyDTO findById(@PathVariable Long id) {
        return studyService.findByID(id);
    }

    // GET /api/board/quest/update/{id} (업데이트 폼 요청)
    @GetMapping("/studies/update/{id}")
    public ResponseEntity<StudyDTO> updateForm(
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") String userId) {
        StudyDTO studyDTO = studyService.findByID(id, userId);
        return ResponseEntity.ok(studyDTO);
    }


    // POST /api/board/coding/update
    @PostMapping("/studies/update")
    public StudyDTO update(@RequestBody StudyDTO studyDTO) {
        return studyService.update(studyDTO); // 업데이트된 CodingDTO 반환
    }

    // DELETE /api/board/quest/delete/{id}
    @DeleteMapping("/studies/delete/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") String userId) {
        boolean isDeleted = studyService.delete(id, userId);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "사용자 권한이 없습니다.");
        }
    }

    // POST /api/board/quest/{id}/like
    @PostMapping("/studies/{id}/like")
    public ResponseEntity<Void> likeQuest(
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") String userId) {
        studyService.increaseLike(id);
        return ResponseEntity.ok().build(); // 200 OK
    }

    // POST /api/board/quest/{id}/scrap
    @PostMapping("/studies/{id}/scrap")
    public ResponseEntity<Void> scrapQuest(
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") String userId) {
        studyService.toggleScrap(id);
        return ResponseEntity.ok().build(); // 200 OK
    }


    @GetMapping("/studies/top-liked")
    public ResponseEntity<List<StudyDTO>> getTopLikedFrees() {
        List<StudyDTO> topLikedFrees = studyService.getTopLikedFrees();
        if (topLikedFrees.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(topLikedFrees); // 200 OK
    }


    @GetMapping("/studies/sort-by-likes")
    public Page<StudyDTO> sortByLikes(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "studyid", required = false) String studyid,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "contentKeyword", required = false) String contentKeyword,
            @RequestParam(value = "hashtagKeyword", required = false) String hashtagKeyword

    ) {

        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "deadline"));
        Page<StudyDTO> studyList;

        // 검색 파라미터가 없으면 단순히 좋아요 순 정렬
        if (    (studyid == null || studyid.isEmpty()) &&
                (searchKeyword == null || searchKeyword.isEmpty()) &&
                (contentKeyword == null || contentKeyword.isEmpty()) &&
                (hashtagKeyword == null || hashtagKeyword.isEmpty())
        ) {
            studyList =  studyService.sortBydeadline(pageable);
            return studyList;
        } else {

            return studyService.searchdeadline(studyid,searchKeyword, contentKeyword, hashtagKeyword, pageable);
        }
    }




    @PostMapping("/studies/{studyId}/apply")
    public ResponseEntity<ApplyResponseDTO> applyToStudy(
            @PathVariable Long studyId,
            @Valid @RequestBody ApplyRequestDTO applyDTO) {

        ApplyEntity applyEntity = studyService.applyToStudy(studyId, applyDTO);
        ApplyResponseDTO responseDTO = new ApplyResponseDTO(
                applyEntity.getId(),
                applyEntity.getApplyUserId(),
                applyEntity.isAccept(),
                applyEntity.getStudyEntity().getId()
        );
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * 지원 신청을 수락하는 API
     *
     * @param applyId 지원 신청의 ID
     * @return 성공 메시지
     */
    @PostMapping("/studies/apply/{applyId}/accept")
    public ResponseEntity<String> acceptApplication(
            @PathVariable Long applyId,
            @RequestHeader("X-USER-ID") String userId) {
        studyService.acceptApplication(userId, applyId);
        return new ResponseEntity<>("지원 신청이 성공적으로 수락되었습니다.", HttpStatus.OK);
    }


    @GetMapping("studies/{studyId}/applicants")
    public ResponseEntity<ApplicantListResponseDTO> getApplicants(
            @PathVariable Long studyId,
            @RequestHeader("X-USER-ID") String userId) {
        ApplicantListResponseDTO responseDTO = studyService.getApplicants(userId, studyId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * 지원 신청을 거절하는 API
     *
     * @param applyId 지원 신청의 ID
     * @param userId  요청 헤더에서 추출한 사용자 ID
     * @return 성공 메시지
     */
    @DeleteMapping("/studies/apply/{applyId}/reject")
    public ResponseEntity<String> rejectApplication(
            @PathVariable Long applyId,
            @RequestHeader("X-USER-ID") String userId) {
        studyService.rejectApplication(userId, applyId);
        return new ResponseEntity<>("지원 신청이 성공적으로 거절되었습니다.", HttpStatus.OK);
    }
}