package com.keeper.homepage.domain.ctf.application;

import static com.keeper.homepage.domain.ctf.dto.request.CreateTeamRequest.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.ctf.dto.request.CreateTeamRequest;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CtfTeamServiceTest extends IntegrationTest {

  @Nested
  @DisplayName("CTF 팀 생성 테스트")
  class CtfTeamCreateTest {

    private Member member;
    private CtfContest ctfContest;

    @BeforeEach
    void setUp() {
      member = memberTestHelper.generate();
      ctfContest = ctfContestTestHelper.generate();
    }

    @Test
    @DisplayName("팀을 생성한 사람은 팀에 가입되어야 한다.")
    public void 팀을_생성한_사람은_팀에_가입되어야_한다() throws Exception {
      //given
      CreateTeamRequest request = builder()
          .name("KEEPER TEAM")
          .description("2024 CTF TEAM")
          .contestId(ctfContest.getId())
          .build();

      //when
      ctfTeamService.createTeam(member, request);

      //then
      assertThat(member.getCtfTeamHasMembers()).hasSize(1);
    }

    @Test
    @DisplayName("해당 대회에서 이미 가입한 팀이 있다면 팀 생성은 실패한다.")
    public void 해당_대회에서_이미_가입한_팀이_있다면_팀_생성은_실패한다() throws Exception {
      member.join(ctfTeamTestHelper.builder().ctfContest(ctfContest).build());

      CreateTeamRequest request = builder()
          .name("KEEPER TEAM")
          .description("2024 CTF TEAM")
          .contestId(ctfContest.getId())
          .build();

      assertThrows(BusinessException.class, () -> {
        ctfTeamService.createTeam(member, request);
      });
    }
  }
}
