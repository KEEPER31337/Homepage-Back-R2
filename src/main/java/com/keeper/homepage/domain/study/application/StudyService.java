package com.keeper.homepage.domain.study.application;

import com.keeper.homepage.domain.study.dao.StudyRepository;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.domain.thumbnail.entity.Thumbnail;
import com.keeper.homepage.global.util.thumbnail.ThumbnailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

  private final StudyRepository studyRepository;
  private final ThumbnailUtil thumbnailUtil;

  public void create(Study study, MultipartFile thumbnail) {
    saveStudyThumbnail(study, thumbnail);
    studyRepository.save(study);
  }

  private void saveStudyThumbnail(Study study, MultipartFile thumbnail) {
    Thumbnail savedThumbnail = thumbnailUtil.saveThumbnail(thumbnail).orElse(null);
    study.changeThumbnail(savedThumbnail);
  }
}
