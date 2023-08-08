package com.keeper.homepage.domain.merit.api;


import com.keeper.homepage.domain.merit.application.MeritLogService;
import com.keeper.homepage.domain.merit.application.MeritTypeService;
import com.keeper.homepage.domain.merit.dto.AddMeritTypeRequest;
import com.keeper.homepage.domain.merit.dto.GiveMeritPointRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merit")
@RequiredArgsConstructor
public class MeritController {

  private final MeritTypeService meritTypeService;
  private final MeritLogService meritLogService;

  @PostMapping
  public ResponseEntity<Void> registerMerit(
      @RequestBody @Valid GiveMeritPointRequest request
  ) {
    meritLogService.recordMerit(request.getAwarderId(), request.getGiverId(), request.getReason());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/type")
  public ResponseEntity<Void> registerMeritType(
      @RequestBody @Valid AddMeritTypeRequest request
  ) {
    meritTypeService.addMeritType(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

}
