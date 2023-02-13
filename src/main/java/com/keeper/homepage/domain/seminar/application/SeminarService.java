package com.keeper.homepage.domain.seminar.application;

import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_NOT_FOUND;
import static java.util.stream.Collectors.joining;

import com.keeper.homepage.domain.seminar.dao.SeminarRepository;
import com.keeper.homepage.domain.seminar.dto.request.SeminarSaveRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarResponse;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeminarService {

  private static final Random RANDOM = new Random();
  private final SeminarRepository seminarRepository;

  @Transactional
  public Long save(SeminarSaveRequest request) {
    return seminarRepository.save(request.toEntity(randomAttendanceCode())).getId();
  }

  public String randomAttendanceCode() {
    int ATTENDANCE_CODE_LENGTH = 4;

    return RANDOM.ints(ATTENDANCE_CODE_LENGTH, 1, 10)
        .mapToObj(i -> ((Integer) i).toString())
        .collect(joining());
  }

  @Transactional
  public void delete(Long seminarId) {
    Seminar seminar = seminarRepository.findById(seminarId)
        .orElseThrow(() -> new BusinessException(seminarId, "seminarId", SEMINAR_NOT_FOUND));
    seminarRepository.delete(seminar);
  }

  // TODO: 2023-02-13 SeminarRepository에서 select query를 사용하는 방법으로 수정할지 고민이다.
  public List<SeminarResponse> findAll() {
    return seminarRepository.findAll().stream()
        .map(SeminarResponse::new)
        .toList();
  }

  public SeminarResponse findById(Long seminarId) {
    return new SeminarResponse(seminarRepository.findById(seminarId)
        .orElseThrow(() -> new BusinessException(seminarId, "seminarId", SEMINAR_NOT_FOUND)));
  }

  public SeminarResponse findByDate(LocalDate dateTime) {
    return new SeminarResponse(seminarRepository.findByOpenTimeBetween(dateTime.atStartOfDay(),
            dateTime.atTime(LocalTime.MAX))
        .orElseThrow(() -> new BusinessException(dateTime, "dateTime", SEMINAR_NOT_FOUND)));
  }
}
