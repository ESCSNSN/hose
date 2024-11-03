package com.example.demo.service;

import com.example.demo.dto.NoticeDTO;
import com.example.demo.entity.NoticeEntity;
import com.example.demo.entity.NoticeFileEntity;
import com.example.demo.repository.NoticeFileRepository;
import com.example.demo.repository.NoticeRepository;
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

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;

    public void save(NoticeDTO noticeDTO) throws IOException {

        if(noticeDTO.getNoticeFile().isEmpty()){
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
                String savePath = "C:/springboot_img/" + storedFileName; // 4. C:/springboot_img/9802398403948_내사진.jpg
//            String savePath = "/Users/사용자이름/springboot_img/" + storedFileName; // C:/springboot_img/9802398403948_내사진.jpg
                noticeFile.transferTo(new File(savePath)); // 5.
                NoticeFileEntity noticeFileEntity = NoticeFileEntity.toNoticeFileEntity(board, originalFilename, storedFileName);
                noticeFileRepository.save(noticeFileEntity);
            }

        }

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

    public NoticeDTO update(NoticeDTO noticeDTO) {
        NoticeEntity noticeEntity = NoticeEntity.toUpdatedEntity(noticeDTO);
        noticeRepository.save(noticeEntity);
        return findByID(noticeDTO.getId());
    }

    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    public Page<NoticeDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() -1;
        int pageLimit = 10; //한 페이지에 보여줄 글 갯수
        Page<NoticeEntity> noticeEntities =
                noticeRepository.findAll(PageRequest.of(page,pageLimit,Sort.by(Sort.Direction.DESC, "id")));

        Page<NoticeDTO> noticeDTOS = noticeEntities.map(notice -> new NoticeDTO(notice.getId(),notice.getUserId(),notice.getNoticeTitle(),notice.getNoticeCreatedTime()));
        return noticeDTOS;
    }
}
