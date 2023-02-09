package com.keeper.homepage.domain.clerk.api;

import com.keeper.homepage.domain.clerk.application.SeminarService;
import com.keeper.homepage.domain.clerk.dto.request.SeminarSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seminar")
public class SeminarController {

  private final SeminarService seminarService;

  @PostMapping("/create")
  public Long create(@RequestBody SeminarSaveRequest request) {
    return seminarService.save(request);
  }

  @DeleteMapping("/{seminarId}")
  public void delete(@PathVariable Long seminarId) {
    seminarService.delete(seminarId);
  }

}
