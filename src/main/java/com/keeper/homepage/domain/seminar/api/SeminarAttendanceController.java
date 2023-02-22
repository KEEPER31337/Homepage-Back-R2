package com.keeper.homepage.domain.seminar.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.application.SeminarAttendanceService;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceResponse;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seminar/attendances")
public class SeminarAttendanceController {

  private final SeminarAttendanceService seminarAttendanceService;

  @PostMapping
  public ResponseEntity<SeminarAttendanceResponse> attendanceSeminar(
      @LoginMember Member member,
      @RequestBody @Valid SeminarAttendanceRequest request) {
    SeminarAttendanceResponse response = seminarAttendanceService.save(member, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
