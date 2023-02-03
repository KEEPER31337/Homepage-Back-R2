package com.keeper.homepage.global.util.mail;

import com.keeper.homepage.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MailUtilTest extends IntegrationTest {

  @Test
  @DisplayName("메일을 보낼 때 에러가 발생하지 않아야 한다.")
  void should_doesNotThrow_when_sendEmail() {
    mailUtil.sendMail(List.of("test@test.com"), "제목", "내용");
  }
}
