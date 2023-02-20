package com.keeper.homepage.domain.seminar.application;

import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_NOT_FOUND;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_TIME_NOT_AVAILABLE;
import static java.util.stream.Collectors.joining;

import com.keeper.homepage.domain.seminar.application.convenience.ValidSeminarFindService;
import com.keeper.homepage.domain.seminar.dao.SeminarRepository;
import com.keeper.homepage.domain.seminar.dto.request.SeminarStartRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceCodeResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarIdResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarListResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarResponse;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
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
  public SeminarIdResponse save() {
    Seminar seminar = Seminar.builder()
        .attendanceCode(randomAttendanceCode())
        .build();
    return new SeminarIdResponse(seminarRepository.save(seminar).getId());
  }

  private String randomAttendanceCode() {
    final int ATTENDANCE_CODE_LENGTH = 4;

    return RANDOM.ints(ATTENDANCE_CODE_LENGTH, 1, 10)
        .mapToObj(i -> ((Integer) i).toString())
        .collect(joining());
  }

  @Transactional
  public SeminarAttendanceCodeResponse start(Long seminarId, SeminarStartRequest request) {
    validCloseTime(request);

    Seminar seminar = seminarRepository.findById(seminarId)
        .orElseThrow(() -> new BusinessException(seminarId, "seminarId", SEMINAR_NOT_FOUND));
    seminar.changeCloseTime(request.attendanceCloseTime(), request.latenessCloseTime());
    return new SeminarAttendanceCodeResponse(seminar.getAttendanceCode());
  }

  private void validCloseTime(SeminarStartRequest request) {
    LocalDateTime attendanceCloseTime = request.attendanceCloseTime();
    LocalDateTime latenessCloseTime = request.latenessCloseTime();

    if (attendanceCloseTime == null && latenessCloseTime == null)
      return;

    requireNonNull(attendanceCloseTime, "attendanceCloseTime", SEMINAR_TIME_NOT_AVAILABLE);
    requireNonNull(latenessCloseTime, "latenessCloseTime", SEMINAR_TIME_NOT_AVAILABLE);

    if (attendanceCloseTime.isAfter(latenessCloseTime)) {
      throw new BusinessException(attendanceCloseTime, "attendanceCloseTime",
          SEMINAR_TIME_NOT_AVAILABLE);
    }
  }

  public static <T> void requireNonNull(T obj, String fieldName, ErrorCode errorCode) {
    if (obj == null)
      throw new BusinessException("null", fieldName, errorCode);
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
