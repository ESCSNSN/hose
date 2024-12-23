package com.example.demo.service;

import com.example.demo.dto.FreeDTO;
import com.example.demo.dto.MainQuestDTO;
import com.example.demo.dto.NoticeDTO;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestService {
    private final QuestRepository questRepository;
    private final QuestFileRepository questFileRepository;

    public void deleteByAdmin(Long id) {
        questRepository.deleteById(id);
    }



    public void save(QuestDTO questDTO) throws IOException {
        // codingFile이 null이거나 비어 있는지 확인
        if (questDTO.getQuestFile() == null || questDTO.getQuestFile().isEmpty()) {
            QuestEntity questEntity = QuestEntity.toSaveEntity(questDTO);
            questRepository.save(questEntity);
        } else {
            QuestEntity questEntity = QuestEntity.toSaveFileEntity(questDTO);
            Long savedId = questRepository.save(questEntity).getId();
            QuestEntity board = questRepository.findById(savedId).get();

            for (MultipartFile questFile : questDTO.getQuestFile()) {
                String originalFilename = questFile.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + originalFilename;
                String savePath = "C:/springboot_img/" + storedFileName;

                // 파일을 지정된 경로에 저장
                questFile.transferTo(new File(savePath));

                // CodingFileEntity 생성 및 저장
                QuestFileEntity questFileEntity = QuestFileEntity.toQuestFileEntity(board, originalFilename, storedFileName);
                questFileRepository.save(questFileEntity);
            }
        }
    }




    @Transactional
    public QuestDTO findByID(Long id) {
        Optional<QuestEntity> optionalQuestEntity = questRepository.findById(id);
        if(optionalQuestEntity.isPresent()) {
            QuestEntity questEntity = optionalQuestEntity.get();
            QuestDTO questDTO = QuestDTO.toQuestDTO(questEntity);
            return questDTO;
        }
        else {
            return null;
        }
    }

    @Transactional
    public QuestDTO findByID(Long id, String userId) {
        QuestEntity quest = questRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "퀘스트를 찾을 수 없습니다."));

        if (!quest.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "퀘스트에 대한 접근 권한이 없습니다.");
        }
        QuestDTO questDTO = QuestDTO.toQuestDTO(quest);
        return questDTO;
    }

    public QuestDTO update(QuestDTO questDTO) {
        QuestEntity questEntity = QuestEntity.toUpdatedEntity(questDTO);
        questRepository.save(questEntity);
        return findByID(questDTO.getId());
    }

    @Transactional
    public boolean delete(Long id, String userId) {
        Optional<QuestEntity> optionalQuest = questRepository.findById(id);
        if (optionalQuest.isPresent()) {
            QuestEntity quest = optionalQuest.get();
            if (quest.getUserId().equals(userId)) {
                questRepository.delete(quest);
                return true;
            }
        }
        return false;
    }

    public Page<QuestDTO> paging(Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0); // 페이지가 음수일 경우 0으로 설정
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수

        // pageable을 사용해 페이지와 정렬을 설정
        Page<QuestEntity> questEntities = questRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));


        return questEntities.map(quest -> new QuestDTO(
                quest.getId(),
                quest.getQuesttitle(),
                quest.getQuestCreatedTime(),
                quest.getQuestLike(),
                quest.getScrap()
        ));
    }


    @Transactional
    public Page<QuestDTO> searchByTitleOrContentOrHashtagOrType(String title, String content, String hashtag,  Pageable pageable) {
        Page<QuestEntity> questEntities = questRepository.findByTitleOrContentsContaining(title, content, hashtag,  pageable);

        // Lazy-loaded 컬렉션을 초기화
        questEntities.forEach(notice -> notice.getQuestFileEntityList().size());

        return questEntities.map(QuestDTO::toQuestDTO);
    }

    @Transactional
    public void increaseLike(Long id) {
        QuestEntity quest = questRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coding not found with id: " + id));
        quest.setQuestLike(quest.getQuestLike() + 1);
        questRepository.save(quest);
    }


    @Transactional
    public void toggleScrap(Long id) {
        QuestEntity free = questRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coding not found with id: " + id));
        free.setScrap(free.getScrap() == 1 ? 0 : 1);
        questRepository.save(free);
    }

    public List<QuestDTO> getTopLikedFrees() {
        int likeThreshold = 10;
        int limit = 3;
        PageRequest pageRequest = PageRequest.of(0, limit);

        List<QuestEntity> topLikedEntities = questRepository.findByQuestLikeGreaterThanEqualOrderByQuestCreatedTimeDesc(likeThreshold, pageRequest);

        return topLikedEntities.stream()
                .map(quest -> new QuestDTO(
                        quest.getId(),
                        quest.getQuesttitle(),
                        quest.getQuestCreatedTime(),
                        quest.getQuestLike(),
                        quest.getScrap()
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public Page<QuestDTO> searchAndSortByLikes(String searchKeyword, String contentKeyword, String hashtagKeyword, Pageable pageable) {
        Page<QuestEntity> questEntities = questRepository.findByTitleOrContentsContaining(
                searchKeyword, contentKeyword, hashtagKeyword, pageable);

        // Lazy-loaded 컬렉션 초기화 (필요 시)
        questEntities.forEach(quest -> quest.getQuestFileEntityList().size());

        return questEntities.map(QuestDTO::toQuestDTO);
    }


    @Transactional
    public Page<QuestDTO> sortByLikes(Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0); // 페이지가 음수일 경우 0으로 설정
        int pageLimit = 10; // 한 페이지에 보여줄 글 갯수

        Pageable pageRequest = PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "questLike")); // 필드 이름 확인

        Page<QuestEntity> questEntities = questRepository.findAll(pageRequest);

        return questEntities.map(quest -> new QuestDTO(
                quest.getId(),
                quest.getQuesttitle(),
                quest.getQuestCreatedTime(),
                quest.getQuestLike(),
                quest.getScrap()
        ));
    }

    @Transactional
    public List<MainQuestDTO> getTop3Quests() {
        PageRequest pageable = PageRequest.of(0, 3);
        List<QuestEntity> quests = questRepository.findTop3QuestsWithFiles(pageable);
        return quests.stream()
                .map(MainQuestDTO::toMainQuestDTO)
                .collect(Collectors.toList());
    }


}
