package com.keeper.homepage.domain.clerk.application;

import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_NOT_FOUND;

import com.keeper.homepage.domain.clerk.dao.seminar.SeminarRepository;
import com.keeper.homepage.domain.clerk.dto.request.SeminarSaveRequest;
import com.keeper.homepage.domain.clerk.dto.response.SeminarResponse;
import com.keeper.homepage.domain.clerk.entity.seminar.Seminar;
import com.keeper.homepage.global.error.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeminarService {

  private final SeminarRepository seminarRepository;

  @Transactional
  public Long save(SeminarSaveRequest request) {
    return seminarRepository.save(request.toEntity()).getId();
  }

  @Transactional
  public void delete(Long seminarId) {
    Seminar seminar = seminarRepository.findById(seminarId)
        .orElseThrow(() -> new BusinessException(seminarId, "seminarId", SEMINAR_NOT_FOUND));
    seminarRepository.delete(seminar);
  }


  public List<SeminarResponse> findAll() {
    return seminarRepository.findAll().stream()
        .map(SeminarResponse::new)
        .toList();
  }
}
