package com.keeper.homepage.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.auth.dto.request.SignUpRequest;
import com.keeper.homepage.domain.member.entity.Member;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SignUpServiceTest extends IntegrationTest {

  private final SignUpRequest validRequest = SignUpRequest.builder()
      .loginId("loginId_1337")
      .email("keeper@keeper.or.kr")
      .password("password123!@#$")
      .realName("정현모minion")
      .nickname("0v0zㅣ존")
      .authCode("0123456789")
      .birthday(LocalDate.of(1970, 1, 1))
      .studentId("197012345")
      .build();

  @Test
  @DisplayName("회원가입 시 비밀번호는 암호화되어야 한다.")
  void should_encrypted_when_signUp() {
    doReturn("").when(signUpService).getActualAuthCode(any());
    doNothing().when(signUpService).checkAuthCodeMatch(any(), any());
    long savedMemberId = signUpService.signUp(validRequest);

    Member savedMember = memberRepository.findById(savedMemberId).orElseThrow();
    String hashedPassword = savedMember.getProfile().getPassword();
    assertThat(hashedPassword).isNotEqualTo(validRequest.getPassword());
    assertThat(passwordEncoder.matches(validRequest.getPassword(), hashedPassword)).isTrue();
  }
}
