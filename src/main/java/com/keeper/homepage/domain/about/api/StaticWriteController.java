package com.keeper.homepage.domain.about.api;

import com.keeper.homepage.domain.about.application.StaticWriteService;
import com.keeper.homepage.domain.about.dto.response.StaticWriteTitleResponse;
import com.keeper.homepage.domain.about.dto.response.StaticWriteTitleTypeResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/about")
public class StaticWriteController {

  private final StaticWriteService staticWriteService;

  @GetMapping("/titles/types")
  public ResponseEntity<StaticWriteTitleTypeResponse> getAllTypes() {
    StaticWriteTitleTypeResponse staticWriteTitles = staticWriteService.getAllTypes();
    return ResponseEntity.status(HttpStatus.OK)
        .body(staticWriteTitles);
  }

  @GetMapping("/titles/types/{type}")
  public ResponseEntity<StaticWriteTitleResponse> getTitleByType(@PathVariable @NotNull String type) {
    StaticWriteTitleResponse response = staticWriteService.getTitleByType(type);
    return ResponseEntity.status(HttpStatus.OK)
        .body(response);
  }

}
