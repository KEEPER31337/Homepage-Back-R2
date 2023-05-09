package com.keeper.homepage.domain.study.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.application.StudyService;
import com.keeper.homepage.domain.study.dto.request.StudyCreateRequest;
import com.keeper.homepage.domain.study.dto.response.StudyDetailResponse;
import com.keeper.homepage.domain.study.dto.response.StudyListResponse;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studies")
public class StudyController {

  private final StudyService studyService;

  @PostMapping
  public ResponseEntity<Void> createStudy(
      @LoginMember Member member,
      @ModelAttribute @Valid StudyCreateRequest request
  ) {
    studyService.create(request.toEntity(member), request.getThumbnail());
    return ResponseEntity.status(HttpStatus.CREATED)
        .build();
  }

  @DeleteMapping("/{studyId}")
  public ResponseEntity<Void> deleteStudy(
      @LoginMember Member member,
      @PathVariable long studyId
  ) {
    studyService.delete(member, studyId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{studyId}")
  public ResponseEntity<StudyDetailResponse> getStudy(
      @PathVariable long studyId
  ) {
    StudyDetailResponse response = studyService.getStudy(studyId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(response);
  }

  @GetMapping
  public ResponseEntity<StudyListResponse> getStudies(
      @RequestParam int year,
      @RequestParam int season
  ) {
    StudyListResponse listResponse = studyService.getStudies(year, season);
    return ResponseEntity.status(HttpStatus.OK)
        .body(listResponse);
  }
}
