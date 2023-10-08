package com.keeper.homepage.domain.seminar.application;

import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ATTENDANCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.BEFORE_ATTENDANCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.getSeminarAttendanceStatusBy;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_IS_DUPLICATED;
import static com.keeper.homepage.global.error.ErrorCode.SEMINAR_TIME_NOT_AVAILABLE;
import static java.util.stream.Collectors.joining;

import com.keeper.homepage.domain.member.application.convenience.MemberFindService;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.application.convenience.ValidSeminarFindService;
import com.keeper.homepage.domain.seminar.dao.SeminarAttendanceRepository;
import com.keeper.homepage.domain.seminar.dao.SeminarRepository;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceCodeResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarDetailResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarIdResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarListResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarResponse;
import com.keeper.homepage.domain.seminar.entity.Seminar;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import com.keeper.homepage.global.error.BusinessException;
import com.keeper.homepage.global.error.ErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
  private final SeminarAttendanceRepository seminarAttendanceRepository;
  private final ValidSeminarFindService validSeminarFindService;
  private final MemberFindService memberFindService;

  @Transactional
  public SeminarIdResponse save(LocalDate openDate) {
    checkDuplicateSeminar(openDate);
    Seminar seminar = seminarRepository.save(Seminar.builder()
        .openTime(openDate.atStartOfDay())
        .build());

    List<Member> regularMembers = memberFindService.findAllRegular();
    regularMembers.forEach(member -> {
      seminarAttendanceRepository.save(
          SeminarAttendance.builder()
              .seminar(seminar)
              .member(member)
              .seminarAttendanceStatus(getSeminarAttendanceStatusBy(BEFORE_ATTENDANCE))
              .attendTime(openDate.atStartOfDay())
              .build());
    });
    return new SeminarIdResponse(seminar.getId());
  }

  private void checkDuplicateSeminar(LocalDate openTime) {
    if (seminarRepository.existsByOpenTime(openTime)) {
      throw new BusinessException(openTime, "openTime", SEMINAR_IS_DUPLICATED);
    }
  }

  private String randomAttendanceCode() {
    final int ATTENDANCE_CODE_LENGTH = 4;

    return RANDOM.ints(ATTENDANCE_CODE_LENGTH, 1, 10)
        .mapToObj(i -> ((Integer) i).toString())
        .collect(joining());
  }

  @Transactional
  public SeminarAttendanceCodeResponse start(Member starter, Long seminarId, LocalDateTime attendanceCloseTime,
      LocalDateTime latenessCloseTime) {
    checkValidCloseTime(attendanceCloseTime, latenessCloseTime);

    Seminar seminar = validSeminarFindService.findById(seminarId);
    seminar.setAttendanceCode(randomAttendanceCode());
    attendanceStarter(seminar, starter);
    seminar.setStarter(starter);
    seminar.changeCloseTime(attendanceCloseTime, latenessCloseTime);
    return new SeminarAttendanceCodeResponse(seminar.getAttendanceCode());
  }

  private void checkValidCloseTime(LocalDateTime attendanceCloseTime, LocalDateTime latenessCloseTime) {
    if (attendanceCloseTime == null && latenessCloseTime == null) {
      return;
    }

    requireNonNull(attendanceCloseTime, "attendanceCloseTime", SEMINAR_TIME_NOT_AVAILABLE);
    requireNonNull(latenessCloseTime, "latenessCloseTime", SEMINAR_TIME_NOT_AVAILABLE);

    if (attendanceCloseTime.isAfter(latenessCloseTime)) {
      throw new BusinessException(attendanceCloseTime, "attendanceCloseTime",
          SEMINAR_TIME_NOT_AVAILABLE);
    }
  }

  private static <T> void requireNonNull(T obj, String fieldName, ErrorCode errorCode) {
    if (obj == null) {
      throw new BusinessException("null", fieldName, errorCode);
    }
  }

  private void attendanceStarter(Seminar seminar, Member member) {
    Optional<SeminarAttendance> seminarAttendance = seminarAttendanceRepository.findBySeminarAndMember(seminar,
        member);
    if (seminarAttendance.isPresent()) {
      SeminarAttendance attendance = seminarAttendance.get();
      attendance.changeStatus(ATTENDANCE);
      return;
    }
    seminarAttendanceRepository.save(SeminarAttendance.builder()
        .seminar(seminar)
        .member(member)
        .seminarAttendanceStatus(getSeminarAttendanceStatusBy(ATTENDANCE))
        .build());
  }

  @Transactional
  public void delete(long seminarId) {
    Seminar seminar = validSeminarFindService.findById(seminarId);
    seminarRepository.delete(seminar);
  }

  public SeminarDetailResponse findById(Member member, long seminarId) {
    Seminar seminar = validSeminarFindService.findById(seminarId);

    var seminarAttendanceStatusType = seminarAttendanceRepository.findBySeminarAndMember(seminar, member)
        .orElse(SeminarAttendance.builder()
            .seminarAttendanceStatus(getSeminarAttendanceStatusBy(BEFORE_ATTENDANCE))
            .build())
        .getSeminarAttendanceStatus()
        .getType();
    return SeminarDetailResponse.from(seminar, seminarAttendanceStatusType);
  }

  public SeminarResponse findByAvailable() {
    LocalDateTime now = LocalDateTime.now();
    return SeminarResponse.from(seminarRepository.findByAvailable(now, now.toLocalDate())
        .orElse(Seminar.builder().build()));
  }

  public SeminarResponse findByDate(LocalDate localDate) {
    return SeminarResponse.from(seminarRepository.findByOpenTime(localDate)
        .orElse(Seminar.builder().build()));
  }

  public SeminarListResponse findAll() {
    return new SeminarListResponse(validSeminarFindService.findAll());
  }

  public SeminarIdResponse getRecentlyDoneSeminar() {
    LocalDate now = LocalDate.now();
    return seminarRepository.findRecentlyDoneSeminar(now)
        .map(Seminar::getId)
        .map(SeminarIdResponse::new)
        .orElse(null);
  }

  public List<SeminarIdResponse> getRecentlyUpcomingSeminars() {
    LocalDate now = LocalDate.now();
    return seminarRepository.findRecentlyUpcomingSeminar(now)
        .stream()
        .map(Seminar::getId)
        .map(SeminarIdResponse::new)
        .toList();
  }
}
