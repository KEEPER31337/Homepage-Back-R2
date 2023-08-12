package com.keeper.homepage.domain.merit.api;


import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.merit.application.MeritLogService;
import com.keeper.homepage.domain.merit.application.MeritTypeService;
import com.keeper.homepage.domain.merit.dto.request.AddMeritTypeRequest;
import com.keeper.homepage.domain.merit.dto.request.GiveMeritPointRequest;
import com.keeper.homepage.domain.merit.dto.request.UpdateMeritTypeRequest;
import com.keeper.homepage.domain.merit.dto.response.MeritTypeResponse;
import com.keeper.homepage.domain.merit.dto.response.SearchMemberMeritLogResponse;
import com.keeper.homepage.domain.merit.dto.response.SearchMeritLogListResponse;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merits")
@RequiredArgsConstructor
@Secured({"ROLE_회장", "ROLE_부회장", "ROLE_사서"})
public class MeritController {

  private final MeritTypeService meritTypeService;
  private final MeritLogService meritLogService;

  @GetMapping
  public ResponseEntity<Page<SearchMeritLogListResponse>> searchMeritLogList(
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero @Max(30) int size
  ) {
    return ResponseEntity
        .ok(meritLogService.findAll(PageRequest.of(page, size))
            .map(SearchMeritLogListResponse::from));
  }

  @GetMapping("/members/{memberId}")
  public ResponseEntity<Page<SearchMemberMeritLogResponse>> findMeritLogByMemberId(
      @PathVariable long memberId,
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero @Max(30) int size
  ) {
    return ResponseEntity.ok(
        meritLogService.findAllByGiverId(PageRequest.of(page, size), memberId)
            .map(SearchMemberMeritLogResponse::from));
  }

  @PostMapping
  public ResponseEntity<Void> registerMerit(
      @RequestBody @Valid GiveMeritPointRequest request,
      @LoginMember Member member
  ) {
    meritLogService.recordMerit(
        request.getAwarderId(),
        member.getId(),
        request.getMeritTypeId());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/types")
  public ResponseEntity<Page<MeritTypeResponse>> searchMeritType(
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero @Max(30) int size
  ) {
    return ResponseEntity.ok(meritTypeService.findAll(PageRequest.of(page, size))
        .map(MeritTypeResponse::from));
  }

  @PostMapping("/types")
  public ResponseEntity<Void> registerMeritType(
      @RequestBody @Valid AddMeritTypeRequest request
  ) {
    meritTypeService.addMeritType(request.getScore(), request.getReason());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/types/{meritTypeId}")
  public ResponseEntity<Void> updateMeritType(
      @PathVariable long meritTypeId,
      @RequestBody @Valid UpdateMeritTypeRequest request) {
    meritTypeService.updateMeritType(
        meritTypeId,
        request.getScore(),
        request.getReason());
    return ResponseEntity.status(HttpStatus.CREATED)
        .location(URI.create("/merits/types/" + meritTypeId))
        .build();
  }

}
