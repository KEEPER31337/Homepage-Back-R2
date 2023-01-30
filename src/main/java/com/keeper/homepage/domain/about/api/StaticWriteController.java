package com.keeper.homepage.domain.about.api;

import com.keeper.homepage.domain.about.application.StaticWriteService;
import com.keeper.homepage.domain.about.dto.response.StaticWriteTitleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/about")
public class StaticWriteController {

  private final StaticWriteService staticWriteService;

  @GetMapping("/types")
  public ResponseEntity<StaticWriteTitleResponse> getAllTypes() {
    StaticWriteTitleResponse staticWriteTitles = staticWriteService.getAllTypes();
    return ResponseEntity.status(HttpStatus.OK)
        .body(staticWriteTitles);
  }

}
