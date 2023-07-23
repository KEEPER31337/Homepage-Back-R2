package com.keeper.homepage.domain.seminar.application.convenience;

import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_NOT_FOUND;

import com.keeper.homepage.domain.seminar.dao.SeminarRepository;
import com.keeper.homepage.domain.seminar.dto.response.SeminarResponse;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.global.error.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ValidSeminarFindService {

  private static final long VIRTUAL_SEMINAR_ID = 1L;
  private final SeminarRepository seminarRepository;

  public List<SeminarResponse> findAll() {
    return seminarRepository.findAllByIdIsNot(VIRTUAL_SEMINAR_ID).stream()
        .map(SeminarResponse::from)
        .toList();
  }

  public Seminar findById(long seminarId) {
    return seminarRepository.findByIdAndIdNot(seminarId, VIRTUAL_SEMINAR_ID)
        .orElseThrow(() -> new BusinessException(seminarId, "seminarId", SEMINAR_NOT_FOUND));
  }
}
