package com.keeper.homepage.domain.seminar.application;

import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_TIME_NOT_AVAILABLE;
import static java.util.stream.Collectors.joining;

import com.keeper.homepage.domain.seminar.application.convenience.ValidSeminarFindService;
import com.keeper.homepage.domain.seminar.dao.SeminarRepository;
import com.keeper.homepage.domain.seminar.dto.request.SeminarSaveRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarCreateResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarListResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarResponse;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.global.error.BusinessException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
  private final ValidSeminarFindService validSeminarFindService;

  @Transactional
  public SeminarCreateResponse save(SeminarSaveRequest request) {
    validCloseTime(request);

    Seminar seminar = request.toEntity(randomAttendanceCode());
    return new SeminarCreateResponse(seminarRepository.save(seminar).getId());
  }

  private void validCloseTime(SeminarSaveRequest request) {
    LocalDateTime attendanceCloseTime = request.getAttendanceCloseTime();
    LocalDateTime latenessCloseTime = request.getLatenessCloseTime();

    if (attendanceCloseTime.isAfter(latenessCloseTime)) {
      throw new BusinessException(attendanceCloseTime, "attendanceCloseTime",
          SEMINAR_TIME_NOT_AVAILABLE);
    }
  }

  private String randomAttendanceCode() {
    final int ATTENDANCE_CODE_LENGTH = 4;

    return RANDOM.ints(ATTENDANCE_CODE_LENGTH, 1, 10)
        .mapToObj(i -> ((Integer) i).toString())
        .collect(joining());
  }

  @Transactional
  public void delete(long seminarId) {
    Seminar seminar = seminarRepository.findById(seminarId)
        .orElseThrow(() -> new BusinessException(seminarId, "seminarId", SEMINAR_NOT_FOUND));
    seminarRepository.delete(seminar);
  }

  public SeminarResponse findById(long seminarId) {
    return new SeminarResponse(seminarRepository.findById(seminarId)
        .orElseThrow(() -> new BusinessException(seminarId, "seminarId", SEMINAR_NOT_FOUND)));
  }

  public SeminarResponse findByDate(LocalDate localDate) {
    return new SeminarResponse(seminarRepository.findByOpenTime(localDate)
        .orElse(Seminar.builder().build()));
  }

  public SeminarListResponse findAll() {
    return new SeminarListResponse(validSeminarFindService.findAll());
  }
}
