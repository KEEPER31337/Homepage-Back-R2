package com.keeper.homepage.domain.seminar.dto.response;

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

  public static SeminarAttendanceManageResponse of(Member member, List<SeminarAttendance> seminarAttendances) {
    return SeminarAttendanceManageResponse.builder()
        .memberId(member.getId())
        .memberName(member.getRealName())
        .generation(member.getGeneration())
        .attendances(seminarAttendances.stream().map(SeminarAttendanceDetailResponse::from).toList())
        .build();
  }
}
