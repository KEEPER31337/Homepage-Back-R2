package com.keeper.homepage.domain.study.application.convenience;

import static com.keeper.homepage.global.error.ErrorCode.STUDY_NOT_FOUND;

import com.keeper.homepage.domain.study.dao.StudyRepository;
import com.keeper.homepage.domain.study.entity.Study;
import com.keeper.homepage.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyFindService {

  private final StudyRepository studyRepository;

  public Study findById(long studyId) {
    return studyRepository.findById(studyId)
        .orElseThrow(() -> new BusinessException(studyId, "studyId", STUDY_NOT_FOUND));
  }
}
