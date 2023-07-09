package com.keeper.homepage.domain.attendance.api;

import com.keeper.homepage.domain.attendance.application.AttendanceService;
import com.keeper.homepage.domain.attendance.dto.response.AttendanceResponse;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.config.security.annotation.LoginMember;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attendances")
@RequiredArgsConstructor
public class AttendanceController {

  private final AttendanceService attendanceService;

  @PostMapping
  public ResponseEntity<Void> create(
      @LoginMember Member member
  ) {
    attendanceService.create(member);
    return ResponseEntity.status(HttpStatus.CREATED)
        .build();
  }

  @GetMapping("/today")
  public ResponseEntity<Page<AttendanceResponse>> getTodayRanks(
      @RequestParam(defaultValue = "0") @PositiveOrZero int page,
      @RequestParam(defaultValue = "10") @PositiveOrZero int size
  ) {
    return ResponseEntity.ok(attendanceService.getTodayRanks(PageRequest.of(page, size)));
  }

  @GetMapping("/continuous")
  public ResponseEntity<List<AttendanceResponse>> getContinuousRanks() {
    return ResponseEntity.ok(attendanceService.getContinuousRanks());
  }
}
