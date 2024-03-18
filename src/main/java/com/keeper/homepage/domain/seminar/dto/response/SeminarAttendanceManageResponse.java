package com.keeper.homepage.domain.seminar.dto.response;

import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ABSENCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.ATTENDANCE;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.LATENESS;
import static com.keeper.homepage.domain.seminar.entity.SeminarAttendanceStatus.SeminarAttendanceStatusType.PERSONAL;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.entity.SeminarAttendance;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PACKAGE)
public class SeminarAttendanceManageResponse {

  private Long memberId;
  private String memberName;
  private String generation;
  private List<SeminarAttendanceDetailResponse> attendances;
  private long totalAttendance;
  private long totalLateness;
  private long totalAbsence;
  private long totalPersonal;

  public static SeminarAttendanceManageResponse of(Member member,
      List<SeminarAttendance> seminarAttendances) {
    return SeminarAttendanceManageResponse.builder()
        .memberId(member.getId())
        .memberName(member.getRealName())
        .generation(member.getGeneration())
        .attendances(
            seminarAttendances.stream().map(SeminarAttendanceDetailResponse::from).toList())
        .totalAttendance(seminarAttendances.stream()
            .filter(seminarAttendance -> seminarAttendance.isStatus(ATTENDANCE))
            .count())
        .totalLateness(seminarAttendances.stream()
            .filter(seminarAttendance -> seminarAttendance.isStatus(LATENESS))
            .count())
        .totalAbsence(seminarAttendances.stream()
            .filter(seminarAttendance -> seminarAttendance.isStatus(ABSENCE))
            .count())
        .totalPersonal(seminarAttendances.stream()
            .filter(seminarAttendance -> seminarAttendance.isStatus(PERSONAL))
            .count())
        .build();
  }
}
