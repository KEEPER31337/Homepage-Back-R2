package com.keeper.homepage.domain.seminar.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.application.SeminarAttendanceService;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceCodeRequest;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceStatusRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceResponse;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    SeminarAttendanceResponse response = seminarAttendanceService.attendance(seminarId, member, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/{seminarId}/attendances/{memberId}") // TODO: 관리자 권한으로 접근 가능하게 설정
  public ResponseEntity<Void> changeAttendanceStatus(
      @PathVariable long seminarId,
      @PathVariable long memberId,
      @RequestBody @Valid SeminarAttendanceStatusRequest request) {
    seminarAttendanceService.changeStatus(seminarId, memberId, request);
    return ResponseEntity.noContent().build();
  }
}
