package com.example.demo.service;

import com.example.demo.dto.NoticeDTO;
import com.example.demo.entity.NoticeEntity;
import com.example.demo.entity.NoticeFileEntity;
import com.example.demo.repository.NoticeFileRepository;
import com.example.demo.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;

    @Autowired
    private S3Client s3Client;

    private final String bucketName = "info0704"; // 버킷 이름으로 교체

    public void save(NoticeDTO noticeDTO) throws IOException {

        if(noticeDTO.getNoticeFile() == null || noticeDTO.getNoticeFile().isEmpty()){
            NoticeEntity noticeEntity = NoticeEntity.toSaveEntity(noticeDTO);
            noticeRepository.save(noticeEntity);

        }

        else {

            NoticeEntity noticeEntity = NoticeEntity.toSaveFileEntity(noticeDTO);
            Long savedId = noticeRepository.save(noticeEntity).getId();
            NoticeEntity board = noticeRepository.findById(savedId).get();
            for (MultipartFile noticeFile : noticeDTO.getNoticeFile()) {


                String originalFilename = noticeFile.getOriginalFilename(); // 2.
                String storedFileName = System.currentTimeMillis() + "_" + originalFilename; // 3.
                uploadFileToNaverCloud(storedFileName, noticeFile);
                NoticeFileEntity noticeFileEntity = NoticeFileEntity.toNoticeFileEntity(board, originalFilename, storedFileName);
                noticeFileRepository.save(noticeFileEntity);
            }

        }

    }

    private void uploadFileToNaverCloud(String key, MultipartFile file) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .acl("public-read") // 필요에 따라 ACL 조정
                .build();

        s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
    }

    @Transactional
    public List<NoticeDTO> finAll() {
        List<NoticeEntity> noticeEntityList = noticeRepository.findAll();
        List<NoticeDTO> noticeDTOList = new ArrayList<>();
        for (NoticeEntity noticeEntity : noticeEntityList) {
            noticeDTOList.add(NoticeDTO.toNoticeDTO(noticeEntity));
        }
        return noticeDTOList;
    }



    @Transactional
    public NoticeDTO findByID(Long id) {
        Optional<NoticeEntity> optionalNoticeEntity = noticeRepository.findById(id);
        if(optionalNoticeEntity.isPresent()) {
            NoticeEntity noticeEntity = optionalNoticeEntity.get();
            NoticeDTO noticeDTO = NoticeDTO.toNoticeDTO(noticeEntity);
            return noticeDTO;
        }
        else {
            return null;
        }
    }

    @Transactional
    public NoticeDTO findByID(Long id, String userId) {
        NoticeEntity notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "퀘스트를 찾을 수 없습니다."));

        if (!notice.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "퀘스트에 대한 접근 권한이 없습니다.");
        }
        NoticeDTO noticeDTO = NoticeDTO.toNoticeDTO(notice);
        return noticeDTO;
    }

    public NoticeDTO update(NoticeDTO noticeDTO) {
        NoticeEntity noticeEntity = NoticeEntity.toUpdatedEntity(noticeDTO);
        noticeRepository.save(noticeEntity);
        return findByID(noticeDTO.getId());
    }

    @Transactional
    public boolean delete(Long id, String userId) {
        Optional<NoticeEntity> optionalQuest = noticeRepository.findById(id);
        if (optionalQuest.isPresent()) {
            NoticeEntity notice = optionalQuest.get();
            if (notice.getUserId().equals(userId)) {
                noticeRepository.delete(notice);
                return true;
            }
        }
        return false;
    }

    public Page<NoticeDTO> paging(Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0);
        int pageLimit = 5;

        Page<NoticeEntity> noticeEntities = noticeRepository.findAllByOrderByIsPinnedDescNoticeCreatedTimeDesc(
                PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "isPinned", "noticeCreatedTime"))
        );

        return noticeEntities.map(notice -> new NoticeDTO(
                notice.getId(),
                notice.getUserId(),
                notice.getNoticeTitle(),
                notice.getNoticeCreatedTime(),
                notice.isPinned()
        ));
    }

    @Transactional
    public Page<NoticeDTO> searchByTitleOrContents(String title, String content, Pageable pageable) {
        Page<NoticeEntity> noticeEntities = noticeRepository.findByTitleOrContentsContaining(title, content, pageable);

        // Lazy-loaded 컬렉션을 초기화
        noticeEntities.forEach(notice -> notice.getNoticeFileEntityList().size());

        return noticeEntities.map(NoticeDTO::toNoticeDTO);
    }


    @Transactional
    public boolean togglePin(Long id) {
        NoticeEntity notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found with id: " + id));
        boolean newPinStatus = !notice.isPinned(); // 핀 상태 반전
        notice.setPinned(newPinStatus);
        noticeRepository.save(notice); // 변경 사항 저장
        return newPinStatus; // 새 핀 상태 반환
    }

    // 기존 코드에 추가
    @Transactional
    public List<NoticeDTO> findAllByUserId(String userId) {
        List<NoticeEntity> noticeEntities = noticeRepository.findByUserId(userId);
        return noticeEntities.stream()
                .map(NoticeDTO::toNoticeDTO)
                .collect(Collectors.toList());
    }



}
