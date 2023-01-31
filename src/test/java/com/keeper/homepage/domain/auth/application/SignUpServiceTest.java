package com.keeper.homepage.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.domain.member.entity.embedded.LoginId;
import com.keeper.homepage.domain.member.entity.embedded.Password;
import com.keeper.homepage.domain.member.entity.embedded.Profile;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SignUpServiceTest extends IntegrationTest {

  private Profile profile;
  private final String rawPassword = "password123!@#$";
  private final String authCode = "0123456789";

  @BeforeEach
  void setupProfile() {
    profile = Profile.builder()
        .loginId(LoginId.from("loginId_1337"))
        .emailAddress("keeper@keeper.or.kr")
        .password(Password.from(rawPassword))
        .realName("정현모minion")
        .nickname("0v0zㅣ존")
        .birthday(LocalDate.of(1970, 1, 1))
        .studentId("197012345")
        .build();
  }

  @Test
  @DisplayName("회원가입 시 비밀번호는 암호화되어야 한다.")
  void should_encrypted_when_signUp() {
    doReturn("").when(signUpService).getActualAuthCode(any());
    doNothing().when(signUpService).checkAuthCodeMatch(any(), any());
    long savedMemberId = signUpService.signUp(profile, authCode);

    Member savedMember = memberRepository.findById(savedMemberId).orElseThrow();
    String hashedPassword = savedMember.getProfile().getPassword().get();
    assertThat(hashedPassword).isNotEqualTo(rawPassword);
    assertThat(passwordEncoder.matches(rawPassword, hashedPassword)).isTrue();
  }
}
