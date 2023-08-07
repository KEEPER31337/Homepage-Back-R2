package com.keeper.homepage.domain.merit.api;


import com.keeper.homepage.domain.merit.application.MeritLogService;
import com.keeper.homepage.domain.merit.application.MeritTypeService;
import com.keeper.homepage.domain.merit.dto.AddMeritTypeRequest;
import com.keeper.homepage.domain.merit.dto.GiveMeritPointRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merit")
@RequiredArgsConstructor
public class MeritLogController {

    private final MeritTypeService meritTypeService;
    private final MeritLogService meritLogService;

    @PostMapping("/register-merit")
    public ResponseEntity<Void> registerMerit(
            @RequestBody @Valid GiveMeritPointRequest request
            ) {
        meritLogService.recordMerit(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/register-type")
    public ResponseEntity<Void> registerMeritType(
            @RequestBody @Valid AddMeritTypeRequest request) {
        meritTypeService.addMeritType(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
