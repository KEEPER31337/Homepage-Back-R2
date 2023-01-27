package com.keeper.homepage.domain.auth.application;

import com.keeper.homepage.domain.auth.dto.request.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpService {

  @Transactional
  public long signUp(SignUpRequest request) {
    return 0;
  }
}
