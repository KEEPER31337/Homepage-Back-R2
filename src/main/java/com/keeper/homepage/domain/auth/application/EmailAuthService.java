package com.keeper.homepage.domain.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

  public static final int EMAIL_EXPIRED_SECONDS = 300; // 5분

  public int emailAuth(String email) {
    return EMAIL_EXPIRED_SECONDS;
  }
}
