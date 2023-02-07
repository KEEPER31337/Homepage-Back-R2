package com.keeper.homepage.domain.auth.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CheckDuplicateServiceTest extends IntegrationTest {

  @Test
  @DisplayName("중복되는 일 경우 true를 반환해야 한다.")
  void should_returnTrue_whenIsDuplicateEmail() {
    Member member = memberTestHelper.generate();

    boolean result = checkDuplicateService.isDuplicateEmail(member.getProfile().getEmailAddress());

    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("중복되는 일 경우 true를 반환해야 한다.")
  void should_returnTrue_whenIsDuplicateLoginId() {
    Member member = memberTestHelper.generate();

    boolean result = checkDuplicateService.isDuplicateLoginId(member.getProfile().getLoginId());

    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("중복되는 일 경우 true를 반환해야 한다.")
  void should_returnTrue_whenIsDuplicateStudentID() {
    Member member = memberTestHelper.generate();

    boolean result = checkDuplicateService.isDuplicateStudentID(member.getProfile().getStudentId());

    assertThat(result).isTrue();
  }
}
