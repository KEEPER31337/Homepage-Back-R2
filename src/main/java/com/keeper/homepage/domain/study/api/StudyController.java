package com.keeper.homepage.domain.study.api;

import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.study.application.StudyService;
import com.keeper.homepage.domain.study.dto.request.StudyCreateRequest;
import com.keeper.homepage.domain.study.dto.request.StudyJoinRequest;
import com.keeper.homepage.domain.study.dto.request.StudyUpdateRequest;
import com.keeper.homepage.domain.study.dto.response.StudyDetailResponse;
import com.keeper.homepage.domain.study.dto.response.StudyListResponse;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studies")
public class StudyController {

  private final StudyService studyService;

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Void> createStudy(
      @LoginMember Member member,
      @RequestPart @Valid StudyCreateRequest request,
      @RequestPart(required = false) MultipartFile thumbnail
  ) {
    studyService.create(request.toEntity(member), thumbnail);
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

  @PutMapping("/{studyId}")
  public ResponseEntity<Void> updateStudy(
      @LoginMember Member member,
      @PathVariable long studyId,
      @RequestBody @Valid StudyUpdateRequest request
  ) {
    studyService.update(member, studyId, request.toEntity());
    return ResponseEntity.status(HttpStatus.CREATED)
        .location(URI.create("/studies/" + studyId))
        .build();
  }

  @PatchMapping("/{studyId}/thumbnail")
  public ResponseEntity<Void> updateStudyThumbnail(
      @LoginMember Member member,
      @PathVariable long studyId,
      @ModelAttribute MultipartFile thumbnail
  ) {
    studyService.updateStudyThumbnail(member, studyId, thumbnail);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{studyId}/members/{memberId}")
  public ResponseEntity<Void> joinStudy(
      @PathVariable long studyId,
      @PathVariable long memberId
  ) {
    studyService.joinStudy(studyId, memberId);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/{studyId}/members")
  public ResponseEntity<Void> joinStudy(
      @PathVariable long studyId,
      @RequestBody @Valid StudyJoinRequest request
  ) {
    studyService.joinStudy(studyId, request.getMemberIds());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/{studyId}/members/{memberId}")
  public ResponseEntity<Void> leaveStudy(
      @PathVariable long studyId,
      @PathVariable long memberId
  ) {
    studyService.leaveStudy(studyId, memberId);
    return ResponseEntity.noContent().build();
  }
}
