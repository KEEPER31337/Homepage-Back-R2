package com.keeper.homepage.domain.seminar.api;

import com.keeper.homepage.domain.seminar.application.SeminarService;
import com.keeper.homepage.domain.seminar.dto.request.SeminarSaveRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarResponse;
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
  public ResponseEntity<Long> createSeminar(@Valid @RequestBody SeminarSaveRequest request) {
    Long seminarId = seminarService.save(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(seminarId);
  }

  @DeleteMapping("/{seminarId}")
  public ResponseEntity<Void> deleteSeminar(@PathVariable long seminarId) {
    seminarService.delete(seminarId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<SeminarResponse>> getAllSeminars() {
    List<SeminarResponse> seminarList = seminarService.findAll();
    return ResponseEntity.status(HttpStatus.OK).body(seminarList);
  }

  // TODO: 2023-02-17 날짜로 조회하는 기능의 권한이 아직 명확하지 않다. 
  // 프론트와 연결하면서 추후에 수정할 예정
  @GetMapping(params = "date")
  @Secured({"ROLE_회장", "ROLE_부회장"})
  public ResponseEntity<SeminarResponse> getSeminarByDate(
      @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localdate) {
    SeminarResponse seminarResponse = seminarService.findByDate(localdate);
    return ResponseEntity.status(HttpStatus.OK).body(seminarResponse);
  }

  @GetMapping("/{seminarId}")
  public ResponseEntity<SeminarResponse> getSeminar(@PathVariable long seminarId) {
    SeminarResponse seminarResponse = seminarService.findById(seminarId);
    return ResponseEntity.status(HttpStatus.OK).body(seminarResponse);
  }
}
