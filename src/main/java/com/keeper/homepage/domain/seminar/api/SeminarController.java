package com.keeper.homepage.domain.seminar.api;

import com.keeper.homepage.domain.seminar.application.SeminarService;
import com.keeper.homepage.domain.seminar.application.convenience.FindService;
import com.keeper.homepage.domain.seminar.dto.request.SeminarSaveRequest;
import com.keeper.homepage.domain.seminar.dto.response.SeminarResponse;
import java.time.LocalDate;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
  private final FindService findService;

  @PostMapping
  public Long createSeminar(@Valid @RequestBody SeminarSaveRequest request) {
    return seminarService.save(request);
  }

  @DeleteMapping("/{seminarId}")
  public void deleteSeminar(@PathVariable long seminarId) {
    seminarService.delete(seminarId);
  }

  @GetMapping
  public List<SeminarResponse> getAllSeminars() {
    return findService.findAll();
  }

  @GetMapping(params = "date")
  public SeminarResponse getSeminarByDate(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    return seminarService.findByDate(date);
  }

  @GetMapping("/{seminarId}")
  public SeminarResponse getSeminar(@PathVariable long seminarId) {
    return seminarService.findById(seminarId);
  }
}