package com.keeper.homepage.domain.merit.api;


import com.keeper.homepage.domain.merit.application.MeritLogService;
import com.keeper.homepage.domain.merit.application.MeritTypeService;
import com.keeper.homepage.domain.merit.dto.request.AddMeritTypeRequest;
import com.keeper.homepage.domain.merit.dto.request.GiveMeritPointRequest;
import com.keeper.homepage.domain.merit.dto.request.UpdateMeritTypeRequest;
import com.keeper.homepage.domain.merit.dto.response.MeritTypeResponse;
import com.keeper.homepage.domain.merit.entity.MeritType;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merits")
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

  @GetMapping("/types")
  public ResponseEntity<Page<MeritTypeResponse>> registerMeritType(
      @PageableDefault(size = 10) Pageable pageable) {
    Page<MeritType> page = meritTypeService.findAll(pageable);
    Page<MeritTypeResponse> map = page.map(MeritTypeResponse::from);
    return ResponseEntity.ok(map);
  }


}
