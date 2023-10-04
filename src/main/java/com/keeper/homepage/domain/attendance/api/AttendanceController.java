package com.keeper.homepage.domain.attendance.api;

import com.keeper.homepage.domain.attendance.application.AttendanceService;
import com.keeper.homepage.domain.attendance.dto.response.AttendanceContinuousRankResponse;
import com.keeper.homepage.domain.attendance.dto.response.AttendanceInfoResponse;
import com.keeper.homepage.domain.attendance.dto.response.AttendancePointResponse;
import com.keeper.homepage.domain.attendance.dto.response.AttendanceResponse;
import com.keeper.homepage.domain.attendance.dto.response.AttendanceTodayRankResponse;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/attendances")
@RequiredArgsConstructor
public class AttendanceController {

  private final AttendanceService attendanceService;

  @GetMapping("/today-rank")
  public ResponseEntity<Page<AttendanceTodayRankResponse>> getTodayRanks(
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero int size
  ) {
    return ResponseEntity.ok(attendanceService.getTodayRanks(PageRequest.of(page, size)));
  }

  @GetMapping("/continuous-rank")
  public ResponseEntity<List<AttendanceContinuousRankResponse>> getContinuousRanks() {
    return ResponseEntity.ok(attendanceService.getContinuousRanks());
  }

  @GetMapping("/point")
  public ResponseEntity<AttendancePointResponse> getTodayAttendancePoint(
      @LoginMember Member member
  ) {
    return ResponseEntity.ok(attendanceService.getTodayAttendancePoint(member));
  }

  @GetMapping("/members/{memberId}/info")
  public ResponseEntity<AttendanceInfoResponse> getAttendanceInfo(
      @PathVariable long memberId
  ) {
    return ResponseEntity.ok(attendanceService.getAttendanceInfo(memberId));
  }

  @GetMapping("/members/{memberId}/total")
  public ResponseEntity<List<AttendanceResponse>> getTotalAttendance(
      @PathVariable long memberId,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate
  ) {
    return ResponseEntity.ok(attendanceService.getTotalAttendance(memberId, localDate));
  }
}
