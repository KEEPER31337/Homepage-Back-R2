package com.keeper.homepage.domain.seminar.api;

import com.keeper.homepage.domain.seminar.application.SeminarService;
import com.keeper.homepage.domain.seminar.dto.request.SeminarSaveRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    return ResponseEntity.status(HttpStatus.OK).body(seminarId);
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

  @GetMapping(params = "date")
  public ResponseEntity<SeminarResponse> getSeminarByDate(
      @RequestParam("date") String strLocalDate) {
    SeminarResponse seminarResponse = seminarService.findByDate(strLocalDate);
    return ResponseEntity.status(HttpStatus.OK).body(seminarResponse);
  }

  @GetMapping("/{seminarId}")
  public ResponseEntity<SeminarResponse> getSeminar(@PathVariable long seminarId) {
    SeminarResponse seminarResponse = seminarService.findById(seminarId);
    return ResponseEntity.status(HttpStatus.OK).body(seminarResponse);
  }
}
