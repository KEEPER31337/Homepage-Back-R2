package com.keeper.homepage.domain.seminar.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.application.SeminarAttendanceService;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceCodeRequest;
import com.keeper.homepage.domain.seminar.dto.request.SeminarAttendanceStatusRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceListResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceStatusResponse;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seminars")
public class SeminarAttendanceController {

  private final SeminarAttendanceService seminarAttendanceService;

  @PostMapping("/{seminarId}/attendances")
  public ResponseEntity<SeminarAttendanceStatusResponse> attendanceSeminar(
      @PathVariable Long seminarId,
      @LoginMember Member member,
      @RequestBody @Valid SeminarAttendanceCodeRequest request) {
    SeminarAttendanceStatusResponse response = seminarAttendanceService.save(seminarId, member, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/{seminarId}/attendances")
  public ResponseEntity<Void> changeAttendanceStatus(
      @PathVariable Long seminarId,
      @LoginMember Member member,
      @RequestBody @Valid SeminarAttendanceStatusRequest request) {
    seminarAttendanceService.changeStatus(seminarId, member, request);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/attendances")
  @Secured({"ROLE_회장", "ROLE_부회장", "ROLE_서기"})
  public ResponseEntity<SeminarAttendanceListResponse> getAllAttendances(@PageableDefault(size = 5) Pageable pageable) {
    // TODO: 2023-03-05 데이터 보내는 방식을 변경해야 함 (repository에 인터페이스 만들어서 효율적으로 조회하기)
    SeminarAttendanceListResponse response = seminarAttendanceService.findAll(pageable);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
