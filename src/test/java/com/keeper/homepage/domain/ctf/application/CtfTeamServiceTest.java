package com.keeper.homepage.domain.ctf.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keeper.homepage.IntegrationTest;
import com.keeper.homepage.domain.ctf.entity.CtfContest;
import com.keeper.homepage.domain.ctf.entity.team.CtfTeam;
import com.keeper.homepage.domain.member.entity.Member;
import com.keeper.homepage.global.error.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CtfTeamServiceTest extends IntegrationTest {

  private Member member;
  private CtfContest ctfContest;

  @BeforeEach
  void setUp() {
    member = memberTestHelper.generate();
    ctfContest = ctfContestTestHelper.generate();
  }

  @Nested
  @DisplayName("CTF 팀 생성 테스트")
  class CtfTeamCreateTest {

    @Test
    @DisplayName("팀을 생성한 사람은 팀에 가입되어야 한다.")
    public void 팀을_생성한_사람은_팀에_가입되어야_한다() throws Exception {
      //when
      ctfTeamService.createTeam(member, "KEEPER TEAM", "2024 CTF TEAM", ctfContest.getId());

      //then
      assertThat(member.getCtfTeamHasMembers()).hasSize(1);
    }

    @Test
    @DisplayName("해당 대회에서 이미 가입한 팀이 있다면 팀 생성은 실패한다.")
    public void 해당_대회에서_이미_가입한_팀이_있다면_팀_생성은_실패한다() throws Exception {
      member.join(ctfTeamTestHelper.builder().ctfContest(ctfContest).build());

      assertThrows(BusinessException.class, () -> {
        ctfTeamService.createTeam(member, "KEEPER TEAM", "2024 CTF TEAM", ctfContest.getId());
      });
    }
  }

  @Nested
  @DisplayName("CTF 팀 수정 테스트")
  class CtfTeamUpdateTest {

    @Test
    @DisplayName("팀원이 아니라면 팀 정보 수정은 실패한다.")
    public void 팀원이_아니라면_팀_정보_수정은_실패한다() throws Exception {
      CtfTeam ctfTeam1 = ctfTeamTestHelper.generate();
      CtfTeam ctfTeam2 = ctfTeamTestHelper.generate();

      member.join(ctfTeam1);

      assertThrows(BusinessException.class, () -> {
        ctfTeamService.updateTeam(member, ctfTeam2.getId(), "팀 이름", "팀 설명");
      });
    }
  }

  @Nested
  @DisplayName("CTF 팀 가입 & 탈퇴 테스트")
  class CtfTeamJoinAndLeaveTest {

    @Test
    @DisplayName("해당 대회에서 이미 가입한 팀이 있다면 팀 가입은 실패한다.")
    public void 해당_대회에서_이미_가입한_팀이_있다면_팀_가입은_실패한다() throws Exception {
      CtfTeam ctfTeam = ctfTeamTestHelper.builder().ctfContest(ctfContest).build();
      member.join(ctfTeam);

      CtfTeam anotherTeam = ctfTeamTestHelper.builder().ctfContest(ctfContest).build();

      assertThrows(BusinessException.class, () -> {
        ctfTeamService.joinTeam(ctfContest.getId(), anotherTeam.getId(), member.getId());
      });
    }
  }
}
