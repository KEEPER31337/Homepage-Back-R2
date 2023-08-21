package com.keeper.homepage.domain.study.application.convenience;

import com.keeper.homepage.domain.study.dao.StudyHasMemberRepository;
import com.keeper.homepage.domain.study.dao.StudyRepository;
import com.keeper.homepage.domain.study.entity.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyDeleteService {

  private final StudyRepository studyRepository;
  private final StudyHasMemberRepository studyHasMemberRepository;

  public void delete(Study study) {
    studyHasMemberRepository.deleteAllByStudy(study);
    studyRepository.delete(study);
  }
}
