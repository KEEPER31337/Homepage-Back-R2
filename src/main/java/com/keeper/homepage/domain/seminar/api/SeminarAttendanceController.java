package com.keeper.homepage.domain.seminar.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.application.SeminarAttendanceService;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceCodeRequest;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceStatusRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceManageResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceResponse;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/seminars")
public class SeminarAttendanceController {

  private final SeminarAttendanceService seminarAttendanceService;

  @PatchMapping("/{seminarId}/attendances")
  public ResponseEntity<SeminarAttendanceResponse> attendanceSeminar(
      @PathVariable Long seminarId,
      @LoginMember Member member,
      @RequestBody @Valid SeminarAttendanceCodeRequest request) {
    SeminarAttendanceResponse response = seminarAttendanceService.attendance(seminarId, member,
        request.attendanceCode());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Secured({"ROLE_회장", "ROLE_부회장", "ROLE_서기"})
  @GetMapping("/attendances")
  public ResponseEntity<Page<SeminarAttendanceManageResponse>> getAttendances(
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero int size
  ) {
    Page<SeminarAttendanceManageResponse> responses = seminarAttendanceService.getAttendances(
        PageRequest.of(page, size));
    return ResponseEntity.ok(responses);
  }

  @PatchMapping("/attendances/{attendanceId}") // TODO: 관리자 권한으로 접근 가능하게 설정
  public ResponseEntity<Void> changeAttendanceStatus(
      @PathVariable long attendanceId,
      @RequestBody @Valid SeminarAttendanceStatusRequest request) {
    seminarAttendanceService.changeStatus(attendanceId, request);
    return ResponseEntity.noContent().build();
  }
}
