package com.keeper.homepage.domain.ctf.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class CtfTeamRepositoryTest extends IntegrationTest {

  @Nested
  @DisplayName("DB Trigger 테스트")
  class DBTest {

    @Test
    @DisplayName("하나의 대회에 같은 이름의 팀을 등록할 수 없다.")
    public void 하나의_대회에_같은_이름의_팀을_등록할_수_없다() throws Exception {
      CtfContest ctfContest = ctfContestTestHelper.generate();
      String teamName = "KEEPER TEAM";

      ctfTeamTestHelper.builder()
          .name(teamName)
          .ctfContest(ctfContest)
          .build();

      assertThrows(DataIntegrityViolationException.class, () -> {
        ctfTeamTestHelper.builder()
            .name(teamName)
            .ctfContest(ctfContest)
            .build();
      });
    }
  }
}
