package com.keeper.homepage.domain.seminar.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.seminar.application.SeminarService;
import com.keeper.homepage.domain.seminar.dto.request.SeminarStartRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarAttendanceCodeResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarDetailResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarIdResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarListResponse;
import com.keeper.homepage.domain.seminar.dto.response.SeminarResponse;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Secured({"ROLE_회장", "ROLE_부회장", "ROLE_서기"})
@RequestMapping("/seminars")
public class SeminarController {

  private final SeminarService seminarService;

  @PostMapping
  public ResponseEntity<SeminarIdResponse> createSeminar(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate openDate
  ) {
    SeminarIdResponse response = seminarService.save(openDate);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/{seminarId}")
  public ResponseEntity<SeminarAttendanceCodeResponse> startSeminar(
      @LoginMember Member member,
      @PathVariable long seminarId,
      @Valid @RequestBody SeminarStartRequest request
  ) {
    SeminarAttendanceCodeResponse response = seminarService.start(member, seminarId,
        request.attendanceCloseTime(),
        request.latenessCloseTime());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{seminarId}")
  public ResponseEntity<Void> deleteSeminar(@PathVariable long seminarId) {
    seminarService.delete(seminarId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<SeminarListResponse> getAllSeminars() {
    SeminarListResponse response = seminarService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/available")
  @Secured({"ROLE_회원"})
  public ResponseEntity<SeminarResponse> availableSeminar() {
    SeminarResponse response = seminarService.findByAvailable();
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // TODO: 2023-02-17 날짜로 조회하는 기능의 권한이 아직 명확하지 않다. 
  // 프론트와 연결하면서 추후에 수정할 예정
  @GetMapping(params = "date")
  @Secured({"ROLE_회장", "ROLE_부회장"})
  public ResponseEntity<SeminarResponse> getSeminarByDate(
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localdate) {
    SeminarResponse response = seminarService.findByDate(localdate);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{seminarId}")
  @Secured({"ROLE_회원"})
  public ResponseEntity<SeminarDetailResponse> getSeminar(
      @LoginMember Member member,
      @PathVariable long seminarId
  ) {
    SeminarDetailResponse response = seminarService.findById(member, seminarId);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/recently-done")
  @Secured({"ROLE_회원"})
  public ResponseEntity<SeminarIdResponse> getRecentlyDoneSeminar() {
    SeminarIdResponse response = seminarService.getRecentlyDoneSeminar();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/recently-upcoming")
  @Secured({"ROLE_회원"})
  public ResponseEntity<List<SeminarIdResponse>> getRecentlyUpcomingSeminars() {
    List<SeminarIdResponse> responses = seminarService.getRecentlyUpcomingSeminars();
    return ResponseEntity.ok(responses);
  }
}
