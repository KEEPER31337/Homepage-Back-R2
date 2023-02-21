package com.keeper.homepage.domain.seminar.application.convenience;

import com.keeper.homepage.domain.seminar.dao.SeminarRepository;
import com.keeper.homepage.domain.seminar.dto.response.SeminarResponse;
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
        .map(SeminarResponse::new)
        .toList();
  }
}
